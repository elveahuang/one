package cc.wdev.platform.security.core;

import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import cc.wdev.platform.commons.enums.OtpTypeEnum;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.security.core.service.CustomUserDetailsService;
import cc.wdev.platform.system.commons.api.CaptchaApi;
import cc.wdev.platform.system.config.api.ConfigApi;
import cc.wdev.platform.system.config.enums.ConfigBizTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * @author elvea
 */
@Slf4j
public class OtpAuthenticationProvider extends BaseAuthenticationProvider {

    private CaptchaApi captchaApi;

    private ConfigApi configApi;

    public OtpAuthenticationProvider(CustomUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return (OtpAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(OtpAuthenticationToken.class, authentication, () ->
            this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports", "Only OtpAuthenticationToken is supported"));

        OtpAuthenticationToken authenticationToken = (OtpAuthenticationToken) authentication;

        // 匹配本地用户
        UserDetails user;
        OtpUser otpUser = this.determinePrincipal(authenticationToken);
        try {
            log.info("Retrieve otp user '{}'", otpUser.getPrincipal());
            user = this.retrieveUser(otpUser, authenticationToken);
        } catch (UsernameNotFoundException ex) {
            log.info("Failed to find otp user '{}'", otpUser.getPrincipal());
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");

        return createSuccessAuthentication(user, authentication, user);
    }

    private OtpUser determinePrincipal(OtpAuthenticationToken authentication) {
        return OtpUser.builder()
            .otpType(authentication.getOtpType())
            .inviteCode(authentication.getInviteCode())
            .mobileCountryCode(authentication.getMobileCountryCode())
            .mobileNumber(authentication.getMobileNumber())
            .email(authentication.getEmail())
            .build();
    }

    protected UserDetails retrieveUser(OtpUser otpUser, OtpAuthenticationToken authentication) throws AuthenticationException {
        try {
            // 校验验证码
            if (!this.checkVerifyCode(authentication)) {
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }

            // 获取用户
            return this.getUserDetailsService().loadUserByOtp(otpUser);
        } catch (UsernameNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * 校验验证码
     */
    public boolean checkVerifyCode(OtpAuthenticationToken authenticationToken) {
        // 是否跳过验证码检查，只能用于本地开发调试
        if (this.configApi.getBoolean(ConfigBizTypeEnum.DEV_PASS_CAPTCHA.getCode(), false)) {
            return true;
        }

        OtpTypeEnum otpTypeEnum = BaseEnum.getEnumByValue(authenticationToken.getOtpType(), OtpTypeEnum.class);
        CaptchaTypeEnum captchaTypeEnum = switch (otpTypeEnum) {
            case SMS -> CaptchaTypeEnum.SMS;
            case EMAIL -> CaptchaTypeEnum.EMAIL;
        };
        return this.captchaApi.check(CaptchaCheckRequest.builder()
            .type(captchaTypeEnum)
            .key(authenticationToken.getVerifyCodeKey())
            .value(authenticationToken.getVerifyCodeValue())
            .email(authenticationToken.getEmail())
            .mobileCountryCode(authenticationToken.getMobileCountryCode())
            .mobileNumber(authenticationToken.getMobileNumber())
            .build());
    }

    private Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        UsernamePasswordAuthenticationToken result = OtpAuthenticationToken.authenticated(
            principal, authentication.getCredentials(), this.authoritiesMapper.mapAuthorities(user.getAuthorities())
        );
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Autowired
    public void setCaptchaApi(CaptchaApi captchaApi) {
        this.captchaApi = captchaApi;
    }

    @Autowired
    public void setConfigApi(ConfigApi configApi) {
        this.configApi = configApi;
    }

}
