package cc.wdev.platform.security.core.web.authentication;

import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import cc.wdev.platform.commons.exception.InvalidCaptchaException;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.security.CustomAuthorizationGrantType;
import cc.wdev.platform.commons.security.CustomParameterNames;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.api.CaptchaApi;
import cc.wdev.platform.system.config.api.ConfigApi;
import cc.wdev.platform.system.config.enums.ConfigBizTypeEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static cc.wdev.platform.commons.constants.SecurityConstants.OAUTH_TOKEN_ENDPOINT;

/**
 * @author elvea
 */
@Slf4j
public class CaptchaAuthenticationFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;

    private final ConfigApi configApi;

    private final CaptchaApi captchaApi;

    private final AuthenticationFailureHandler failureHandler;

    public CaptchaAuthenticationFilter(
        ConfigApi configApi,
        CaptchaApi captchaApi,
        AuthenticationFailureHandler failureHandler
    ) {
        this.configApi = configApi;
        this.captchaApi = captchaApi;
        this.failureHandler = failureHandler;
        this.requestMatcher = PathPatternRequestMatcher.withDefaults().matcher(OAUTH_TOKEN_ENDPOINT);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        // 检查当前请求是否需要校验验证码
        if (!this.requestMatcher.matches(request)) {
            chain.doFilter(request, response);
            return;
        }

        // 检查系统是否启用验证码
        if (!this.configApi.getBoolean(ConfigBizTypeEnum.LOGIN_CAPTCHA_ENABLED.getCode(), false)) {
            chain.doFilter(request, response);
            return;
        }

        // 密码模式才需要做验证码验证
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (CustomAuthorizationGrantType.PASSWORD.getValue().equalsIgnoreCase(grantType)) {
            String captchaKey = request.getParameter(CustomParameterNames.CAPTCHA_KEY);
            String captchaValue = request.getParameter(CustomParameterNames.CAPTCHA_VALUE);

            try {
                if (StringUtils.isEmpty(captchaKey) || StringUtils.isEmpty(captchaValue)) {
                    throw new InvalidCaptchaException("Invalid Captcha.");
                }

                CaptchaCheckRequest captchaCheckRequest = CaptchaCheckRequest.builder()
                    .type(CaptchaTypeEnum.CODE)
                    .key(captchaKey)
                    .value(captchaValue)
                    .build();
                if (!this.captchaApi.check(captchaCheckRequest)) {
                    throw new InvalidCaptchaException("Invalid Captcha.");
                }
            } catch (AuthenticationException ex) {
                this.failureHandler.onAuthenticationFailure(request, response, ex);
                return;
            }
        }
        chain.doFilter(request, response);
    }

}
