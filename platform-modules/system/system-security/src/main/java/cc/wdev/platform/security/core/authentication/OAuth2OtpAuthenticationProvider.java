package cc.wdev.platform.security.core.authentication;

import cc.wdev.platform.commons.security.CustomAuthorizationGrantType;
import cc.wdev.platform.commons.security.CustomParameterNames;
import cc.wdev.platform.security.core.OtpAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

/**
 * @author elvea
 */
@Slf4j
public class OAuth2OtpAuthenticationProvider extends OAuth2BaseAuthenticationProvider {

    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    public OAuth2OtpAuthenticationProvider(AuthenticationManager authenticationManager,
                                           OAuth2AuthorizationService authorizationService,
                                           OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return OAuth2OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        OAuth2OtpAuthenticationToken authenticationToken = (OAuth2OtpAuthenticationToken) authentication;

        // 验证客户端是否已认证
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClient(authenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 检查当前客户端是否包含社区模式
        if (registeredClient == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        if (!registeredClient.getAuthorizationGrantTypes().contains(CustomAuthorizationGrantType.OTP)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        // 验证获取用户登录凭证
        OtpAuthenticationToken otpAuthenticationToken = getOtpAuthentication(authenticationToken);
        Authentication otpAuthentication = authenticationManager.authenticate(otpAuthenticationToken);

        // 验证授权范围
        Set<String> requestedScopes = authenticationToken.getScopes();
        Set<String> authorizedScopes = validateScopes(requestedScopes, registeredClient);

        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
            .registeredClient(registeredClient)
            .principal(otpAuthentication)
            .authorizationServerContext(AuthorizationServerContextHolder.getContext())
            .authorizedScopes(authorizedScopes)
            .authorizationGrantType(CustomAuthorizationGrantType.OTP)
            .authorizationGrant(authenticationToken);

        // ----- Access token -----
        log.info("Create access token by grant type {}.", CustomAuthorizationGrantType.OTP);
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the access token.", OAuth2ParameterNames.ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
            generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
            generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());

        // ----- Access token -----
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
            .principalName(otpAuthentication.getName())
            .authorizationGrantType(CustomAuthorizationGrantType.OTP)
            .authorizedScopes(authorizedScopes)
            .attribute(Principal.class.getName(), otpAuthentication);
        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken, (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)
            && !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {

            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the refresh token.", OAuth2ParameterNames.ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }
            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;

            authorizationBuilder.refreshToken(refreshToken);
        }

        // ----- ID token -----
        OidcIdToken idToken;
        if (requestedScopes.contains(OidcScopes.OPENID)) {
            tokenContext = tokenContextBuilder.tokenType(ID_TOKEN_TOKEN_TYPE).authorization(authorizationBuilder.build()).build();
            OAuth2Token generatedIdToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedIdToken instanceof Jwt)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the ID token.", OAuth2ParameterNames.ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }
            idToken = new OidcIdToken(generatedIdToken.getTokenValue(), generatedIdToken.getIssuedAt(), generatedIdToken.getExpiresAt(), ((Jwt) generatedIdToken).getClaims());
            authorizationBuilder.token(idToken, (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));
        } else {
            idToken = null;
        }

        // 生成并保持授权记录
        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);
        log.trace("Saved authorization for grant type {}.", CustomAuthorizationGrantType.OTP);

        Map<String, Object> additionalParameters = idTokenAdditionalParameters(idToken);
        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
    }

    private OtpAuthenticationToken getOtpAuthentication(OAuth2OtpAuthenticationToken OAuth2OtpAuthenticationToken) {
        Map<String, Object> parameters = OAuth2OtpAuthenticationToken.getAdditionalParameters();
        String otpType = MapUtils.getString(parameters, CustomParameterNames.OTP_TYPE, "");
        String mobileCountryCode = MapUtils.getString(parameters, CustomParameterNames.MOBILE_COUNTRY_CODE, "");
        String mobileNumber = MapUtils.getString(parameters, CustomParameterNames.MOBILE_NUMBER, "");
        String email = MapUtils.getString(parameters, CustomParameterNames.EMAIL, "");
        String verifyCodeKey = MapUtils.getString(parameters, CustomParameterNames.VERIFY_CODE_KEY, "");
        String verifyCodeValue = MapUtils.getString(parameters, CustomParameterNames.VERIFY_CODE_VALUE, "");

        return new OtpAuthenticationToken(
            otpType, mobileCountryCode, mobileNumber, email, verifyCodeKey, verifyCodeValue, parameters
        );
    }

}
