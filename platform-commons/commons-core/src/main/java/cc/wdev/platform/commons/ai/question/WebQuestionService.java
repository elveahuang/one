package cc.wdev.platform.commons.ai.question;

import cc.wdev.platform.commons.ai.domain.request.AiAnswerSubmitRequest;
import cc.wdev.platform.commons.ai.domain.vo.AiQuestionVo;
import org.springaicommunity.agent.tools.AskUserQuestionTool;
import org.springframework.http.codec.ServerSentEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 问答管理器
 *
 * @author dfox
 */
public interface WebQuestionService {

    /**
     * 处理向用户提问，推送问题至客户端并等待用户提交答案
     *
     * @param conversationId 会话ID
     * @param userId         用户ID
     * @param tenantId       会话所属租户（来自聊天请求，缺省时回退当前上下文租户）
     */
    Map<String, String> handleQuestions(String conversationId, Long userId, Long tenantId, List<AskUserQuestionTool.Question> questions);

    /**
     * 提交用户回答并唤醒等待中的工具调用
     */
    boolean submitAnswers(AiAnswerSubmitRequest request);

    /**
     * 注册当前会话的 SSE 消息发射器
     */
    void registerEmitter(String conversationId, Consumer<ServerSentEvent<String>> emitter);

    /**
     * 注销并清理会话上下文
     *
     * @param conversationId 会话ID
     * @param tenantId       会话所属租户（来自聊天请求，缺省时回退当前上下文租户）
     */
    void removeSession(String conversationId, Long tenantId);

    /**
     * 获取指定会话当前挂起的问题（校验归属，仅本人可查看）
     */
    AiQuestionVo getPendingQuestion(String conversationId, Long userId, Long tenantId);

}
