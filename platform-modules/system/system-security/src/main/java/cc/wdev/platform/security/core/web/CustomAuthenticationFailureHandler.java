package cc.wdev.platform.security.core.web;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.InvalidCaptchaException;
import cc.wdev.platform.commons.utils.WebServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author elvea
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final HttpMessageConverter<OAuth2Error> converter = new OAuth2ErrorHttpMessageConverter();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.info("CustomAuthenticationFailureHandler.onAuthenticationFailure...");

        if (e instanceof OAuth2AuthenticationException) {
            ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
            httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
            OAuth2Error error = ((OAuth2AuthenticationException) e).getError();
            this.converter.write(error, null, httpResponse);
        } else {
            if (e instanceof InvalidCaptchaException) {
                WebServletUtils.renderJson(response, R.fail(ResponseCodeEnum.INVALID_CAPTCHA));
            } else {
                WebServletUtils.renderJson(response, R.error(ResponseCodeEnum.ERROR));
            }
        }
    }

}
