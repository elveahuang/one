package cc.wdev.platform.security.core.authentication;

import cc.wdev.platform.commons.security.CustomAuthorizationGrantType;
import cc.wdev.platform.commons.security.CustomParameterNames;
import cc.wdev.platform.commons.security.utils.OAuth2EndpointUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author elvea
 */
public class OAuth2SocialAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!CustomAuthorizationGrantType.SOCIAL.getValue().equals(grantType)) {
            return null;
        }

        // client (REQUIRED)
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (clientPrincipal == null) {
            OAuth2EndpointUtils.throwError(
                OAuth2ErrorCodes.INVALID_REQUEST,
                OAuth2ErrorCodes.INVALID_CLIENT,
                OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);

        // 检测社交登录一次性验证码
        OAuth2EndpointUtils.checkRequiredParameter(parameters, CustomParameterNames.CODE);

        // scope (OPTIONAL)
        String scope = OAuth2EndpointUtils.checkOptionalParameter(parameters, OAuth2ParameterNames.SCOPE);

        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }

        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) && !key.equals(OAuth2ParameterNames.REFRESH_TOKEN) && !key.equals(OAuth2ParameterNames.SCOPE)) {
                additionalParameters.put(key, value.getFirst());
            }
        });

        return new OAuth2SocialAuthenticationToken(clientPrincipal, requestedScopes, additionalParameters);
    }

}
