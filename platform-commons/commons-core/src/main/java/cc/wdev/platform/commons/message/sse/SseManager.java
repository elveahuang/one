package cc.wdev.platform.commons.message.sse;

import cc.wdev.platform.commons.message.session.UserSession;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 本机 SSE 会话与异步等待句柄管理接口
 *
 * @author elvea
 */
public interface SseManager {

    /**
     * 建立 Servlet SseEmitter 连接
     */
    SseEmitter connect(String userId, String sessionId, UserSession userSession);

    /**
     * 关闭并移除 Servlet SseEmitter 连接
     */
    void close(String userId, String sessionId);

    /**
     * 注册本机 SSE 事件发射器
     */
    void registerEmitter(String conversationId, Consumer<ServerSentEvent<String>> emitter);

    /**
     * 判断本机是否存在活跃 SSE 发射器
     */
    boolean hasEmitter(String conversationId);

    /**
     * 向本机 SSE 发射器推送事件
     */
    boolean emitLocal(String conversationId, String event, String data);

    /**
     * 注册本机等待中的异步结果句柄
     *
     * @return true=注册成功；false=该会话已存在挂起提问（双开窗口场景）
     */
    boolean registerFuture(String conversationId, CompletableFuture<Map<String, String>> future);

    /**
     * 移除本机等待中的异步结果句柄
     */
    void removeFuture(String conversationId);

    /**
     * 按值移除本机等待中的异步结果句柄（仅注册者可移除自己的 future，不误删后来者）
     */
    void removeFuture(String conversationId, CompletableFuture<Map<String, String>> future);

    /**
     * 判断本机是否存在等待中的异步结果句柄
     */
    boolean hasPendingFuture(String conversationId);

    /**
     * 完成本机等待中的异步结果句柄
     */
    boolean completeLocalFuture(String conversationId, Map<String, String> answers);

    /**
     * 关闭并清理本机会话句柄
     */
    void closeLocalSession(String conversationId);

}
