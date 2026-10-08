package cc.wdev.platform.base.configuration;

import cc.wdev.platform.commons.core.exchange.HttpExchangeManager;
import cc.wdev.platform.system.commons.api.CaptchaApi;
import cc.wdev.platform.system.commons.api.SocialApi;
import cc.wdev.platform.system.config.api.ConfigApi;
import cc.wdev.platform.system.core.api.LoginSessionApi;
import cc.wdev.platform.system.core.api.TenantApi;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.security.api.AuthorizationApi;
import cc.wdev.platform.system.security.api.AuthorizationConsentApi;
import cc.wdev.platform.system.security.api.ClientApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cc.wdev.platform.system.commons.constants.SystemConstants.SYSTEM_SERVICE_HTTP_EXCHANGE;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class PlatformHttpExchangeConfiguration {

    private final HttpExchangeManager httpExchangeManager;

    @Bean
    @ConditionalOnMissingBean(UserApi.class)
    public UserApi userApi() {
        return this.httpExchangeManager.getHttpExchange(UserApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

    @Bean
    @ConditionalOnMissingBean(CaptchaApi.class)
    public CaptchaApi captchaApi() {
        return this.httpExchangeManager.getHttpExchange(CaptchaApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

    @Bean
    @ConditionalOnMissingBean(SocialApi.class)
    public SocialApi socialApi() {
        return this.httpExchangeManager.getHttpExchange(SocialApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

    @Bean
    @ConditionalOnMissingBean(TenantApi.class)
    public TenantApi tenantApi() {
        return this.httpExchangeManager.getHttpExchange(TenantApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

    @Bean
    @ConditionalOnMissingBean(ClientApi.class)
    public ClientApi clientApi() {
        return this.httpExchangeManager.getHttpExchange(ClientApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

    @Bean
    @ConditionalOnMissingBean(ConfigApi.class)
    public ConfigApi configApi() {
        return this.httpExchangeManager.getHttpExchange(ConfigApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

    @Bean
    @ConditionalOnMissingBean(LoginSessionApi.class)
    public LoginSessionApi loginSessionApi() {
        return this.httpExchangeManager.getHttpExchange(LoginSessionApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationConsentApi.class)
    public AuthorizationConsentApi authorizationConsentApi() {
        return this.httpExchangeManager.getHttpExchange(AuthorizationConsentApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationApi.class)
    public AuthorizationApi authorizationApi() {
        return this.httpExchangeManager.getHttpExchange(AuthorizationApi.class, SYSTEM_SERVICE_HTTP_EXCHANGE);
    }

}
