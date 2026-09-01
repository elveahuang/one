package cc.wdev.platform.commons.autoconfigure.data;

import cc.wdev.platform.commons.autoconfigure.data.properties.DataCoreProperties;
import cc.wdev.platform.commons.autoconfigure.data.properties.DataJdbcProperties;
import cc.wdev.platform.commons.data.core.auditor.UserAuditorAware;
import cc.wdev.platform.commons.data.jdbc.dao.Dao;
import cc.wdev.platform.commons.data.jdbc.dialect.DbDialect;
import cc.wdev.platform.commons.data.jdbc.utils.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({DataCoreProperties.class, DataJdbcProperties.class})
public class DataCoreAutoConfiguration {

    public DataCoreAutoConfiguration(DataCoreProperties dataCoreProperties, DataJdbcProperties dataJdbcProperties) {
        log.info("DataCoreAutoConfiguration is enabled");
        log.info("DataCoreAutoConfiguration core {}", dataCoreProperties.isEnabled() ? "enabled" : "disabled");
        log.info("DataCoreAutoConfiguration jdbc {}", dataJdbcProperties.isEnabled() ? "enabled" : "disabled");
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(AuditorAware.class)
    @ConditionalOnProperty(prefix = DataCoreProperties.PREFIX, name = "enabled", havingValue = "true")
    public static class CoreConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public UserAuditorAware userAuditorAware() {
            return new UserAuditorAware();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(JdbcTemplate.class)
    @ConditionalOnProperty(prefix = DataJdbcProperties.PREFIX, name = "enabled", havingValue = "true")
    public static class JdbcConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public DbDialect dbDialect(@Autowired(required = false) JdbcTemplate jdbcTemplate) {
            return jdbcTemplate.execute(JdbcUtils::getDialect);
        }

        @Bean
        @ConditionalOnMissingBean
        public Dao dao(@Autowired(required = false) JdbcTemplate jdbcTemplate) {
            return new Dao(jdbcTemplate);
        }

    }

}
