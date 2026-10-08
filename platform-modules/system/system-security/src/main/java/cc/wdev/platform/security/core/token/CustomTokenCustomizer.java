package cc.wdev.platform.security.core.token;

import cc.wdev.platform.commons.constants.SecurityConstants;
import cc.wdev.platform.commons.security.domain.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * @author elvea
 */
@Slf4j
@Component
@AllArgsConstructor
public class CustomTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            if (context.getPrincipal().getPrincipal() instanceof User user) {
                context.getClaims().claims(claims -> {
                    claims.put(SecurityConstants.JWT_KEY_UID, user.getId());
                    claims.put(SecurityConstants.JWT_KEY_TID, user.getTenantId());
                    claims.put(SecurityConstants.JWT_KEY_USERNAME, user.getName());
                });
            }
        } else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            if (context.getPrincipal().getPrincipal() instanceof User user) {
                context.getClaims().claims(claims -> {
                    claims.put(SecurityConstants.JWT_KEY_UID, user.getId());
                    claims.put(SecurityConstants.JWT_KEY_TID, user.getTenantId());
                });
            }
        }
    }

}
