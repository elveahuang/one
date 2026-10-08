package cc.wdev.platform.system.core.api;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.core.domain.dto.LoginSessionDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.LocalDateTime;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/login-session")
public interface LoginSessionApi {

    /**
     * 保存登录会话
     */
    @PostExchange("/save-login-session")
    R<Boolean> saveLoginSession(@RequestBody LoginSessionDto loginSession) throws Exception;

    /**
     * 获取单位时间内登录人次
     */
    long getAllLoginCount(LocalDateTime startTime, LocalDateTime endTime);

}
