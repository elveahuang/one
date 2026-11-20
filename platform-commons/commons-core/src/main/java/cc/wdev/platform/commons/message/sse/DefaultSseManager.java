package cc.wdev.platform.commons.message.sse;

import cc.wdev.platform.commons.message.session.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * 默认本机 SSE 会话与异步等待句柄管理器实现
 *
 * @author elvea
 */
@Slf4j
public class DefaultSseManager implements SseManager {

    private final ConcurrentMap<String, Consumer<ServerSentEvent<String>>> emitters = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Map<String, UserSession>> userSessionMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CompletableFuture<Map<String, String>>> pendingFutures = new ConcurrentHashMap<>();

    @Override
    public SseEmitter connect(String userId, String sessionId, UserSession userSession) {
        SseEmitter emitter = new SseEmitter(0L);
        this.sseEmitters.put(sessionId, emitter);
        this.userSessionMap.computeIfAbsent(userId, _ -> new ConcurrentHashMap<>()).put(sessionId, userSession);
        emitter.onCompletion(() -> close(userId, sessionId));
        emitter.onTimeout(() -> close(userId, sessionId));
        emitter.onError(_ -> close(userId, sessionId));
        return emitter;
    }

    @Override
    public void close(String userId, String sessionId) {
        SseEmitter emitter = this.sseEmitters.remove(sessionId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        }
        Map<String, UserSession> sessions = this.userSessionMap.get(userId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                this.userSessionMap.remove(userId);
            }
        }
    }

    @Override
    public void registerEmitter(String conversationId, Consumer<ServerSentEvent<String>> emitter) {
        if (conversationId != null && !conversationId.isBlank() && emitter != null) {
            this.emitters.put(conversationId, emitter);
        }
    }

    @Override
    public boolean hasEmitter(String conversationId) {
        return conversationId != null && this.emitters.containsKey(conversationId);
    }

    @Override
    public boolean emitLocal(String conversationId, String event, String data) {
        Consumer<ServerSentEvent<String>> emitter = this.emitters.get(conversationId);
        if (emitter == null) {
            return false;
        }
        emitter.accept(ServerSentEvent.builder(data).event(event != null ? event : "text").build());
        return true;
    }

    @Override
    public boolean registerFuture(String conversationId, CompletableFuture<Map<String, String>> future) {
        if (conversationId != null && !conversationId.isBlank() && future != null) {
            // 同一会话同时只允许一个挂起提问，双开窗口的第二次注册被拒绝
            return this.pendingFutures.putIfAbsent(conversationId, future) == null;
        }
        return false;
    }

    @Override
    public void removeFuture(String conversationId) {
        if (conversationId != null) {
            this.pendingFutures.remove(conversationId);
        }
    }

    @Override
    public void removeFuture(String conversationId, CompletableFuture<Map<String, String>> future) {
        if (conversationId != null && future != null) {
            // 按值删除：finally 清理时只移除自己注册的 future，不误删后来注册者
            this.pendingFutures.remove(conversationId, future);
        }
    }

    @Override
    public boolean hasPendingFuture(String conversationId) {
        return conversationId != null && this.pendingFutures.containsKey(conversationId);
    }

    @Override
    public boolean completeLocalFuture(String conversationId, Map<String, String> answers) {
        CompletableFuture<Map<String, String>> future = this.pendingFutures.remove(conversationId);
        if (future != null && !future.isDone()) {
            return future.complete(answers != null ? answers : Map.of());
        }
        return false;
    }

    @Override
    public void closeLocalSession(String conversationId) {
        if (conversationId == null) {
            return;
        }
        this.emitters.remove(conversationId);
        CompletableFuture<Map<String, String>> future = this.pendingFutures.remove(conversationId);
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
    }

}
