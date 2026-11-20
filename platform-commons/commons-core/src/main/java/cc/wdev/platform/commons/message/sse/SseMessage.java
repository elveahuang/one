package cc.wdev.platform.commons.message.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * SSE 跨节点广播消息体
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SseMessage implements Serializable {

    public static final String TYPE_EMIT = "EMIT";

    public static final String TYPE_ANSWER = "ANSWER";

    public static final String TYPE_CLOSE = "CLOSE";

    /**
     * 消息动作类型：EMIT（推送 SSE 事件）/ ANSWER（唤醒提问等待）/ CLOSE（关闭清理）
     */
    private String actionType;

    /**
     * 会话标识
     */
    private String conversationId;

    /**
     * SSE event 名称（如 text / error）
     */
    private String event;

    /**
     * SSE 推送文本负载
     */
    private String data;

    /**
     * 用户提交的提问答案（actionType = ANSWER 时携带）
     */
    private Map<String, String> answers;

}
