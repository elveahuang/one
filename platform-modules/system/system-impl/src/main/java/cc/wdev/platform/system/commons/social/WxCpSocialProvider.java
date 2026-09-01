package cc.wdev.platform.system.commons.social;

import cc.wdev.platform.commons.oapis.weixin.service.WxCpManager;
import cc.wdev.platform.commons.security.domain.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
@Component
public class WxCpSocialProvider implements SocialProvider {

    private final ObjectProvider<WxCpManager> service;

    public WxCpSocialProvider(ObjectProvider<WxCpManager> service) {
        this.service = service;
    }

    public SocialUser auth(Map<String, Object> parameters) {
        return null;
    }

}
