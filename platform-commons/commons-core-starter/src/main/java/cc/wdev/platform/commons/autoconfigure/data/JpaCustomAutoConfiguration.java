package cc.wdev.platform.commons.autoconfigure.data;

import cc.wdev.platform.commons.autoconfigure.core.CoreAutoConfiguration;
import cc.wdev.platform.commons.autoconfigure.core.properties.CoreProperties;
import cc.wdev.platform.commons.autoconfigure.data.properties.JpaCustomProperties;
import cc.wdev.platform.commons.core.GlobalContext;
import cc.wdev.platform.commons.data.jpa.id.CustomIdentifierGenerator;
import cc.wdev.platform.commons.data.jpa.repository.BaseEntityRepositoryImpl;
import cc.wdev.platform.commons.data.jpa.tenant.TenantIdResolver;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration(after = {CoreAutoConfiguration.class})
@ConditionalOnClass({HibernateJpaAutoConfiguration.class})
@ConditionalOnProperty(prefix = JpaCustomProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(JpaCustomProperties.class)
@EnableJpaAuditing
@EnableJpaRepositories(repositoryBaseClass = BaseEntityRepositoryImpl.class)
public class JpaCustomAutoConfiguration {

    private final JpaCustomProperties properties;

    private final GlobalContext context;

    public JpaCustomAutoConfiguration(JpaCustomProperties properties, GlobalContext context) {
        log.info("JpaCustomAutoConfiguration is enabled");
        log.info("JpaCustomAutoConfiguration Tenancy {}", context.getTenancy().isEnabled() ? "enabled" : "disabled");

        this.properties = properties;
        this.context = context;
    }

    @Bean("jpaIdentifierGenerator")
    @ConditionalOnMissingBean
    public IdentifierGenerator jpaIdentifierGenerator() {
        return new CustomIdentifierGenerator();
    }

    @Bean("jpaTenantIdentifierResolver")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CoreProperties.TENANCY_PREFIX, name = "enabled", havingValue = "true")
    public CurrentTenantIdentifierResolver<Long> jpaTenantIdentifierResolver() {
        return new TenantIdResolver();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Hibernate Configuration Customizer
    // -----------------------------------------------------------------------------------------------------------------

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
        @Qualifier("jpaTenantIdentifierResolver") @Autowired(required = false) CurrentTenantIdentifierResolver<Long> jpaTenantIdentifierResolver
    ) {
        log.info("Create Hibernate Configuration Customizer");
        log.info("Hibernate showSql - {}", this.properties.isShowSql());
        log.info("Hibernate formatSql - {}", this.properties.isFormatSql());

        return properties -> {
            properties.put("hibernate.show_sql", this.properties.isShowSql());
            properties.put("hibernate.format_sql", this.properties.isFormatSql());

            // 开启多租户解析器
            if (this.context.getTenancy().isEnabled() && jpaTenantIdentifierResolver == null) {
                log.info("Tenancy is enabled, but jpaTenantIdentifierResolver is null.");
            } else if (this.context.getTenancy().isEnabled()) {
                log.info("Tenancy is enabled, init CurrentTenantIdentifierResolver.");
                properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, jpaTenantIdentifierResolver);
            }
        };
    }

}
