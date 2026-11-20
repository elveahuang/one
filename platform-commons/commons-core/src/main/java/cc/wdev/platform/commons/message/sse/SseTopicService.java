package cc.wdev.platform.commons.message.sse;

import cc.wdev.platform.commons.core.cache.utils.RedissonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * SSE 跨节点 Topic 广播服务（对标 AbstractTopicService）
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class SseTopicService {

    public static final String DEFAULT_SSE_TOPIC = "platform:ai:sse:topic";

    @Nullable
    private final RedissonUtils redissonUtils;

    private final SseManager sseManager;

    @PostConstruct
    public void init() {
        if (this.redissonUtils == null) {
            log.info("RedissonUtils is absent; SseTopicService runs in local-only mode.");
            return;
        }
        log.info("Init SSE topic listener on [{}]", DEFAULT_SSE_TOPIC);
        this.redissonUtils.addListener(DEFAULT_SSE_TOPIC, SseMessage.class, (channel, msg) -> {
            this.handle(msg);
        });
    }

    /**
     * 发布跨节点 SSE 消息
     */
    public void publish(SseMessage message) {
        if (message == null || message.getConversationId() == null || this.redissonUtils == null) {
            return;
        }
        this.redissonUtils.getTopic(DEFAULT_SSE_TOPIC).publish(message);
    }

    /**
     * 推送 SSE 事件（本机存在则直推，否则广播由持有该连接的节点推送）
     */
    public void emit(String conversationId, String event, String data) {
        if (this.sseManager.emitLocal(conversationId, event, data)) {
            return;
        }
        this.publish(SseMessage.builder()
            .actionType(SseMessage.TYPE_EMIT)
            .conversationId(conversationId)
            .event(event)
            .data(data)
            .build());
    }

    /**
     * 唤醒等待中的提问 Future（本机存在则直接完成，否则广播到持有该 Future 的节点）
     */
    public void completeAnswer(String conversationId, Map<String, String> answers) {
        if (this.sseManager.completeLocalFuture(conversationId, answers)) {
            return;
        }
        this.publish(SseMessage.builder()
            .actionType(SseMessage.TYPE_ANSWER)
            .conversationId(conversationId)
            .answers(answers)
            .build());
    }

    /**
     * 处理收到的集群广播消息
     */
    public void handle(SseMessage message) {
        if (message == null || message.getConversationId() == null) {
            return;
        }
        String cid = message.getConversationId();
        switch (message.getActionType()) {
            case SseMessage.TYPE_EMIT -> {
                if (this.sseManager.emitLocal(cid, message.getEvent(), message.getData())) {
                    log.debug("SSE topic delivered EMIT to local session: conversationId={}", cid);
                }
            }
            case SseMessage.TYPE_ANSWER -> {
                if (this.sseManager.completeLocalFuture(cid, message.getAnswers())) {
                    log.info("SSE topic delivered ANSWER to local waiting future: conversationId={}", cid);
                }
            }
            case SseMessage.TYPE_CLOSE -> this.sseManager.closeLocalSession(cid);
            default -> log.warn("Unknown SseMessage actionType: {}", message.getActionType());
        }
    }

}
