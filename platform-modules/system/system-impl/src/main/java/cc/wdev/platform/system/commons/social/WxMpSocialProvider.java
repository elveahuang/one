package cc.wdev.platform.system.commons.social;

import cc.wdev.platform.commons.enums.LangTypeEnum;
import cc.wdev.platform.commons.enums.SocialTypeEnum;
import cc.wdev.platform.commons.oapis.weixin.service.WxMpManager;
import cc.wdev.platform.commons.security.CustomParameterNames;
import cc.wdev.platform.commons.security.domain.SocialUser;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Component;

import java.util.Map;

import static cc.wdev.platform.commons.security.utils.OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI;

/**
 * @author elvea
 */
@Slf4j
@Component
public class WxMpSocialProvider implements SocialProvider {

    private final ObjectProvider<WxMpManager> service;

    public WxMpSocialProvider(ObjectProvider<WxMpManager> service) {
        this.service = service;
    }

    public SocialUser auth(Map<String, Object> parameters) {
        String code = (String) parameters.get(CustomParameterNames.CODE);
        String inviteCode = (String) parameters.get(CustomParameterNames.INVITE_CODE);

        WxMpManager wxMpManager = this.service.getIfAvailable();
        if (wxMpManager == null) {
            return null;

        }

        try {
            WxOAuth2Service oAuth2Service = wxMpManager.getService().getOAuth2Service();
            WxOAuth2AccessToken accessToken = oAuth2Service.getAccessToken(code);
            WxOAuth2UserInfo userInfo = oAuth2Service.getUserInfo(accessToken, LangTypeEnum.ZH_CN.getValue());
            log.info("WechatMpAuthenticator authenticate wechat user. openId-[{}], sex-[{}]", userInfo.getOpenid(), userInfo.getSex());

            return SocialUser.builder()
                .inviteCode(inviteCode)
                .socialType(SocialTypeEnum.WECHAT_MP.getValue())
                .openId(userInfo.getOpenid())
                .unionId(userInfo.getUnionId())
                .nickname(userInfo.getNickname())
                .headImgUrl(userInfo.getHeadImgUrl())
                .sex(userInfo.getSex())
                .build();
        } catch (WxErrorException e) {
            log.error("WechatMpAuthenticator authenticate wechat user failed. code-[{}]", code, e);
            throw new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "Wechat authenticate failed.", ACCESS_TOKEN_REQUEST_ERROR_URI)
            );
        }
    }

}
