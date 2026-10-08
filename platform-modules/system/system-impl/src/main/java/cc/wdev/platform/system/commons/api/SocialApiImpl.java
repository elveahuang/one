package cc.wdev.platform.system.commons.api;

import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.SocialTypeEnum;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.commons.utils.SpringUtils;
import cc.wdev.platform.system.commons.social.SocialProvider;
import cc.wdev.platform.system.commons.social.WxMpSocialProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author elvea
 */
@Service
@AllArgsConstructor
public class SocialApiImpl implements SocialApi {

    /**
     * @see SocialApi#retrieveSocialUser(String, Map)
     */
    @Override
    public SocialUser retrieveSocialUser(String socialType, Map<String, Object> parameters) {
        SocialProvider socialProvider = this.getProvider(socialType);
        return socialProvider.auth(parameters);
    }

    /**
     * 获取社交登录提供者
     */
    public SocialProvider getProvider(String socialType) {
        SocialTypeEnum socialTypeEnum = BaseEnum.getEnumByValue(socialType, SocialTypeEnum.class);
        return switch (socialTypeEnum) {
            case WECHAT_MP -> SpringUtils.getBean(WxMpSocialProvider.class);
            case WEWORK -> SpringUtils.getBean(SocialProvider.class);
            default -> null;
        };
    }

}
