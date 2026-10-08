package cc.wdev.platform.commons.message.sse;

import cc.wdev.platform.commons.message.session.UserSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
public class SseManager {

    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    private final Map<String, Map<String, UserSession>> userSessionMap = new ConcurrentHashMap<>();

    /**
     * 建立连接
     */
    public SseEmitter connect(String userId, String sessionId, UserSession UserSession) {

        // 保存会话
        SseEmitter emitter = new SseEmitter(86400000L);
        this.emitterMap.put(sessionId, emitter);

        // 保存用户会话
        this.userSessionMap.putIfAbsent(userId, new ConcurrentHashMap<>());
        this.userSessionMap.get(userId).put(sessionId, UserSession);

        // 事件处理
        emitter.onCompletion(() -> {
            SseEmitter e = this.emitterMap.remove(sessionId);
            if (e != null) {
                e.complete();
            }
        });
        emitter.onTimeout(() -> {
            SseEmitter e = this.emitterMap.remove(sessionId);
            if (e != null) {
                e.complete();
            }
        });
        emitter.onError((t) -> {
            SseEmitter e = this.emitterMap.remove(sessionId);
            if (e != null) {
                e.complete();
            }
        });

        try {
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            this.emitterMap.remove(sessionId);
        }
        return emitter;
    }

    /**
     * 注销会话
     */
    public void close(String userId, String sessionId) {
        if (userId == null || sessionId == null) {
            return;
        }

        try {
            SseEmitter sseEmitter = emitterMap.remove(sessionId);
            sseEmitter.send(SseEmitter.event().comment("disconnected"));
            sseEmitter.complete();
        } catch (Exception e) {
            log.info("Close SSE Emitter [{}] failed", userId);
        }


        Map<String, UserSession> sessions = this.userSessionMap.get(userId);
        if (MapUtils.isNotEmpty(sessions)) {
            boolean result = sessions.remove(sessionId) != null;
            log.info("Close SSE User Session [{}] [{}] - {}", userId, sessionId, result);

            if (sessions.isEmpty()) {
                this.userSessionMap.remove(userId);
                log.info("Close all SSE User Session [{}]", userId);
            }
        }
    }

}
