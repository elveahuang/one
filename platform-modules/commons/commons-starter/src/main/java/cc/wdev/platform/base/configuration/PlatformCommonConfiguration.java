package cc.wdev.platform.base.configuration;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.core.tenant.TenantStore;
import cc.wdev.platform.system.commons.constants.SystemConstants;
import cc.wdev.platform.system.core.api.TenantApi;
import cc.wdev.platform.system.core.support.DefaultTenantStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author elvea
 */
@Slf4j
@EnableAspectJAutoProxy
@Configuration(proxyBeanMethods = false)
public class PlatformCommonConfiguration {

    /**
     * 默认租户存储实现
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantStore tenantStore(TenantApi tenantApi) {
        return new DefaultTenantStore(tenantApi);
    }


    /**
     * 默认多语言服务
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
            SystemConstants.I18N_SECURITY_MESSAGES,
            SystemConstants.I18N_LABEL_MESSAGES,
            SystemConstants.I18N_VALIDATION_MESSAGES
        );
        messageSource.setDefaultEncoding(GlobalConstants.ENCODING);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

}
