package cc.wdev.platform.security.core.web;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.utils.WebServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * @author elvea
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        log.error("CustomAuthenticationEntryPoint.", e);
        if (e instanceof BadCredentialsException) {
            WebServletUtils.renderJson(response, R.fail(ResponseCodeEnum.USER__INVALID_USERNAME_OR_PASSWORD));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            WebServletUtils.renderJson(response, R.fail(ResponseCodeEnum.UNAUTHORIZED));
        }
    }

}
