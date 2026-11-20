package cc.wdev.platform.commons.ai.question;

import cc.wdev.platform.commons.ai.domain.request.AiAnswerSubmitRequest;
import cc.wdev.platform.commons.ai.domain.vo.AiQuestionVo;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.message.sse.SseManager;
import cc.wdev.platform.commons.message.sse.SseTopicService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springaicommunity.agent.tools.AskUserQuestionTool;
import org.springframework.http.codec.ServerSentEvent;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * AI 向用户提问交互管理服务实现（支持多节点分布式）
 *
 * @author dfox
 */
@Slf4j
@RequiredArgsConstructor
public class WebQuestionServiceImpl implements WebQuestionService {

    private static final String PENDING_QUESTION_KEY_PREFIX = "ai:question:pending:";

    private final CacheService cacheService;

    private final SseManager sseManager;

    private final SseTopicService sseTopicService;

    /**
     * 等待用户作答的超时分钟数（多题评测逐题作答耗时较长，经 AiProperties 可配置）
     */
    private final long timeoutMinutes;

    /**
     * 构建租户隔离的缓存 key，遵循租户安全 key 生成规范
     */
    private String buildCacheKey(Long tenantId, String conversationId) {
        return PENDING_QUESTION_KEY_PREFIX + (tenantId != null ? tenantId + ":" : "") + conversationId;
    }

    /**
     * 解析当前租户：优先 TenantContext，兜底 SecurityUtils
     */
    private Long resolveTenantId() {
        return TenantContext.getTenantId() != null ? TenantContext.getTenantId() : SecurityUtils.getTid();
    }

    @Override
    public Map<String, String> handleQuestions(String conversationId, Long userId, Long tenantId, List<AskUserQuestionTool.Question> questions) {
        if (StringUtils.isEmpty(conversationId) || CollectionUtils.isEmpty(questions)) {
            log.warn("handleQuestions invalid conversationId [{}] or empty questions [{}]", conversationId, questions);
            return Collections.emptyMap();
        }

        // 优先使用聊天请求携带的租户：模型工具线程无请求上下文（MDC 为空），回退值会取到根租户，
        // 与提交回答路径（Servlet 线程按机构解析）不一致会导致挂起提问缓存 key 错位
        Long effTenantId = (tenantId != null && tenantId > 0) ? tenantId : this.resolveTenantId();
        log.info("handleQuestions start: conversationId={}, userId={}, tenantId={}, questionsCount={}", conversationId, userId, effTenantId, questions.size());
        CompletableFuture<Map<String, String>> future = new CompletableFuture<>();
        String cacheKey = this.buildCacheKey(effTenantId, conversationId);

        // 同一会话同时只允许一个挂起提问（双开窗口场景），注册失败直接返回让模型走未回答分支
        if (!sseManager.registerFuture(conversationId, future)) {
            log.warn("handleQuestions rejected: a pending question already exists for conversationId={}", conversationId);
            return Collections.emptyMap();
        }
        AiQuestionVo questionVo = toVo(conversationId, userId, questions);
        cacheService.set(cacheKey, questionVo, Duration.ofMinutes(timeoutMinutes));

        try {
            String jsonRenderBlock = buildJsonRenderBlock(conversationId, userId, questions);
            String ssePayload = AiUtils.getJsonRenderContent(jsonRenderBlock);
            log.info("Pushing json-render question card to client stream: conversationId={}", conversationId);
            sseTopicService.emit(conversationId, "text", ssePayload);

            // 等待用户通过任意节点提交答案
            return future.get(timeoutMinutes, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            log.warn("Timeout waiting for user answer: conversationId={}", conversationId);
            return Collections.emptyMap();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted waiting for user answer: conversationId={}", conversationId);
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("Error waiting for user answer: conversationId={}", conversationId, e);
            return Collections.emptyMap();
        } finally {
            // 按值删除，只清理自己注册的 future，不误删后来注册者
            sseManager.removeFuture(conversationId, future);
            cacheService.delete(cacheKey);
        }
    }

    @Override
    public boolean submitAnswers(AiAnswerSubmitRequest request) {
        if (request == null || StringUtils.isEmpty(request.getConversationId())) {
            log.warn("submitAnswers rejected: request is null or conversationId is empty");
            return false;
        }

        String conversationId = request.getConversationId();
        Long tenantId = this.resolveTenantId();
        String cacheKey = this.buildCacheKey(tenantId, conversationId);
        AiQuestionVo pendingVo = cacheService.get(cacheKey);
        if (pendingVo == null) {
            log.warn("submitAnswers rejected: no pending question session for conversationId={}, tenantId={}, cacheKey={}", conversationId, tenantId, cacheKey);
            return false;
        }

        // 如果会话指定了 userId 且入参也传了 userId，进行匹配校验
        if (pendingVo.getUserId() != null && request.getUserId() != null && !pendingVo.getUserId().equals(request.getUserId())) {
            log.warn("submitAnswers rejected: userId mismatch for conversationId={}, sessionUserId={}, requestUserId={}",
                conversationId, pendingVo.getUserId(), request.getUserId());
            return false;
        }

        cacheService.delete(cacheKey);
        Map<String, String> answers = request.getAnswers() != null ? request.getAnswers() : Collections.emptyMap();
        // 本机有挂起 future 则直接完成；否则走集群广播唤醒工具所在节点。
        // 本机与广播都无法投递时返回失败，前端提示重试，避免删了缓存却唤不醒 future 的静默分叉
        if (sseManager.hasPendingFuture(conversationId) && sseManager.completeLocalFuture(conversationId, answers)) {
            log.info("submitAnswers success(local): conversationId={}, answersCount={}", conversationId, answers.size());
            return true;
        }
        try {
            sseTopicService.completeAnswer(conversationId, answers);
            log.info("submitAnswers success(topic): conversationId={}, answersCount={}", conversationId, answers.size());
            return true;
        } catch (Exception e) {
            log.error("submitAnswers failed to deliver answer: conversationId={}", conversationId, e);
            return false;
        }
    }

    @Override
    public void registerEmitter(String conversationId, Consumer<ServerSentEvent<String>> emitter) {
        sseManager.registerEmitter(conversationId, emitter);
    }

    @Override
    public void removeSession(String conversationId, Long tenantId) {
        if (StringUtils.isNotEmpty(conversationId)) {
            sseManager.closeLocalSession(conversationId);
            Long effTenantId = (tenantId != null && tenantId > 0) ? tenantId : this.resolveTenantId();
            cacheService.delete(this.buildCacheKey(effTenantId, conversationId));
        }
    }

    @Override
    public AiQuestionVo getPendingQuestion(String conversationId, Long userId, Long tenantId) {
        if (StringUtils.isEmpty(conversationId)) {
            return null;
        }
        // 归属校验：仅会话本人可查看挂起问题
        AiQuestionVo vo = cacheService.get(this.buildCacheKey(tenantId, conversationId));
        if (vo == null) {
            return null;
        }
        if (vo.getUserId() != null && userId != null && !vo.getUserId().equals(userId)) {
            log.warn("getPendingQuestion ownership mismatch: conversationId={}, voUserId={}, userId={}",
                conversationId, vo.getUserId(), userId);
            return null;
        }
        return vo;
    }

    private AiQuestionVo toVo(String conversationId, Long userId, List<AskUserQuestionTool.Question> questions) {
        List<AiQuestionVo.QuestionItemVo> items = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(questions)) {
            for (AskUserQuestionTool.Question q : questions) {
                List<AiQuestionVo.OptionItemVo> options = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(q.options())) {
                    for (AskUserQuestionTool.Question.Option opt : q.options()) {
                        options.add(AiQuestionVo.OptionItemVo.builder()
                            .label(opt.label())
                            .description(opt.description())
                            .build());
                    }
                }
                items.add(AiQuestionVo.QuestionItemVo.builder()
                    .question(q.question())
                    .header(q.header())
                    .multiSelect(q.multiSelect())
                    .options(options)
                    .build());
            }
        }

        return AiQuestionVo.builder()
            .conversationId(conversationId)
            .userId(userId)
            .questions(items)
            .build();
    }

    /**
     * 构建适用于前端 Markdown jsonRender 插件的 json-render 交互块
     */
    private String buildJsonRenderBlock(String conversationId, Long userId, List<AskUserQuestionTool.Question> questions) {
        if (CollectionUtils.isEmpty(questions)) {
            return "{}";
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("type", "Question");

        Map<String, Object> props = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(conversationId)) {
            props.put("conversationId", conversationId);
        }
        if (userId != null && userId > 0) {
            props.put("userId", String.valueOf(userId));
        }

        List<Map<String, Object>> questionDataList = new ArrayList<>();
        for (AskUserQuestionTool.Question q : questions) {
            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("question", q.question());
            qMap.put("header", StringUtils.isNotEmpty(q.header()) ? q.header() : "问题澄清");
            List<Map<String, String>> qOptions = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(q.options())) {
                for (AskUserQuestionTool.Question.Option opt : q.options()) {
                    Map<String, String> optionMap = new LinkedHashMap<>();
                    optionMap.put("label", opt.label());
                    optionMap.put("description", opt.description() != null ? opt.description() : "");
                    qOptions.add(optionMap);
                }
            }
            qMap.put("options", qOptions);
            qMap.put("multiSelect", Boolean.TRUE.equals(q.multiSelect()));
            questionDataList.add(qMap);
        }

        props.put("QuestionData", questionDataList);
        root.put("props", props);
        return GsonUtils.toJson(root);
    }
}
