package cc.wdev.platform.commons.message.sse.web;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.message.session.UserSession;
import cc.wdev.platform.commons.message.sse.SseManager;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 顶层抽象控制器
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSseController extends AbstractController implements DisposableBean {

    private final SseManager sseManager;

    @GetMapping(value = "/start", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        String sessionId = StringUtils.uuid();
        String userId = String.valueOf(SecurityUtils.getUid());

        UserSession UserSession = new UserSession();
        UserSession.setSid(SecurityUtils.getSid());
        UserSession.setUid(SecurityUtils.getUid());
        UserSession.setUsername(SecurityUtils.getUsername());
        UserSession.setLast(System.currentTimeMillis());

        return sseManager.connect(userId, sessionId, UserSession);
    }

    @GetMapping(value = "/close")
    public R<Void> close() {
        String sessionId = StringUtils.uuid();
        String userId = String.valueOf(SecurityUtils.getUid());
        sseManager.close(userId, sessionId);
        return R.success();
    }

    @Override
    public void destroy() throws Exception {
        //
    }

}
