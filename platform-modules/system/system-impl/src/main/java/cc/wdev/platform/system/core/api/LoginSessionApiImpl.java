package cc.wdev.platform.system.core.api;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.core.domain.dto.LoginSessionDto;
import cc.wdev.platform.system.core.service.LoginSessionRabbitService;
import cc.wdev.platform.system.core.service.LoginSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Component
@AllArgsConstructor
public class LoginSessionApiImpl implements LoginSessionApi {

    private final LoginSessionRabbitService loginSessionAmqpService;

    private final LoginSessionService loginSessionService;

    /**
     * @see LoginSessionApi#saveLoginSession(LoginSessionDto)
     */
    @Override
    public R<Boolean> saveLoginSession(LoginSessionDto loginSession) throws Exception {
        this.loginSessionAmqpService.send(loginSession);
        return R.success(Boolean.TRUE);
    }

    @Override
    public long getAllLoginCount(LocalDateTime startTime, LocalDateTime endTime) {
        return loginSessionService.getAllLoginCount(startTime, endTime);
    }

}
