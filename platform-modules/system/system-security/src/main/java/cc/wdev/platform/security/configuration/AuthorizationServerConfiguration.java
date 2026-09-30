package cc.wdev.platform.security.configuration;

import cc.wdev.platform.security.core.authentication.*;
import cc.wdev.platform.security.core.web.authentication.CaptchaAuthenticationFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;

import java.util.Arrays;
import java.util.List;

import static cc.wdev.platform.commons.constants.SecurityConstants.*;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfiguration {

    private final JwtDecoder jwtDecoder;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    private final CaptchaAuthenticationFilter captchaAuthenticationFilter;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final AccessDeniedHandler accessDeniedHandler;

    private final OAuth2AuthorizationService authorizationService;

    private final OAuth2TokenGenerator<?> tokenGenerator;

    private AuthenticationManager authenticationManager;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {
        log.info("Creating authorizationServerSecurityFilterChain...");

        http.oauth2AuthorizationServer(authorizationServer -> {
                http.securityMatcher(authorizationServer.getEndpointsMatcher());
                authorizationServer.oidc(Customizer.withDefaults()).tokenEndpoint(tokenEndpoint -> {
                    tokenEndpoint.accessTokenRequestConverters(authenticationConverters -> authenticationConverters.addAll(authenticationConverterList()));
                    tokenEndpoint.accessTokenResponseHandler(authenticationSuccessHandler);
                    tokenEndpoint.errorResponseHandler(authenticationFailureHandler);
                });
            }).authorizeHttpRequests((authorize) ->
                authorize.anyRequest().authenticated()
            ).cors(Customizer.withDefaults())
            .addFilterAfter(this.captchaAuthenticationFilter, CsrfFilter.class)
            .exceptionHandling(e -> {
                e.authenticationEntryPoint(authenticationEntryPoint);
                e.accessDeniedHandler(accessDeniedHandler);
            }).oauth2ResourceServer((rsc) -> rsc.jwt(jc -> {
                jc.decoder(jwtDecoder);
                jc.jwtAuthenticationConverter(this.jwtAuthenticationConverter);
            }));

        // 添加自定义授权模式实现
        this.addAuthenticationProvider(http);

        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .authorizationEndpoint(OAUTH_AUTHORIZATION_ENDPOINT)
            .deviceAuthorizationEndpoint(OAUTH_DEVICE_AUTHORIZATION_ENDPOINT)
            .deviceVerificationEndpoint(OAUTH_DEVICE_VERIFICATION_ENDPOINT)
            .tokenEndpoint(OAUTH_TOKEN_ENDPOINT)
            .tokenIntrospectionEndpoint(OAUTH_TOKEN_INTROSPECTION_ENDPOINT)
            .tokenRevocationEndpoint(OAUTH_TOKEN_REVOCATION_ENDPOINT)
            .jwkSetEndpoint(OAUTH_JWK_SET_ENDPOINT)
            .oidcUserInfoEndpoint(OAUTH_OIDC_USER_INFO_ENDPOINT)
            .oidcLogoutEndpoint(OAUTH_OIDC_LOGOUT_ENDPOINT)
            .oidcClientRegistrationEndpoint(OAUTH_OIDC_CLIENT_REGISTRATION_ENDPOINT)
            .build();
    }

    private List<AuthenticationConverter> authenticationConverterList() {
        return Arrays.asList(
            new OAuth2OtpAuthenticationConverter(),
            new OAuth2PasswordAuthenticationConverter(),
            new OAuth2SocialAuthenticationConverter()
        );
    }

    private void addAuthenticationProvider(HttpSecurity http) {
        // 密码模式
        OAuth2PasswordAuthenticationProvider OAuth2PasswordAuthenticationProvider = new OAuth2PasswordAuthenticationProvider(
            this.authenticationManager, this.authorizationService, this.tokenGenerator);
        http.authenticationProvider(OAuth2PasswordAuthenticationProvider);

        // 验证码模式
        OAuth2OtpAuthenticationProvider OAuth2OtpAuthenticationProvider = new OAuth2OtpAuthenticationProvider(
            this.authenticationManager, this.authorizationService, this.tokenGenerator);
        http.authenticationProvider(OAuth2OtpAuthenticationProvider);

        // 社区模式
        OAuth2SocialAuthenticationProvider OAuth2SocialAuthenticationProvider = new OAuth2SocialAuthenticationProvider(
            this.authenticationManager, this.authorizationService, this.tokenGenerator);
        http.authenticationProvider(OAuth2SocialAuthenticationProvider);
    }

    @Autowired
    @Qualifier("userAuthenticationManager")
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

}
