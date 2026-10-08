package cc.wdev.platform.system.commons.api;

import cc.wdev.platform.commons.security.domain.SocialUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/social")
public interface SocialApi {

    /**
     * 获取社交用户信息
     */
    @PostExchange("/auth")
    SocialUser retrieveSocialUser(@RequestParam("type") String type,
                                  @RequestBody Map<String, Object> parameters);

}
