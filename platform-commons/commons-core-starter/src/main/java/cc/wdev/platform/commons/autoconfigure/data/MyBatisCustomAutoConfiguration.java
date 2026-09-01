package cc.wdev.platform.commons.autoconfigure.data;

import cc.wdev.platform.commons.autoconfigure.core.CoreAutoConfiguration;
import cc.wdev.platform.commons.autoconfigure.core.properties.CoreProperties;
import cc.wdev.platform.commons.autoconfigure.data.properties.MyBatisCustomProperties;
import cc.wdev.platform.commons.core.GlobalContext;
import cc.wdev.platform.commons.core.tenant.TenantConfig;
import cc.wdev.platform.commons.core.tenant.TenantConfigCustomizer;
import cc.wdev.platform.commons.data.mybatis.handler.CustomMetaObjectHandler;
import cc.wdev.platform.commons.data.mybatis.handler.CustomTenantLineHandler;
import cc.wdev.platform.commons.data.mybatis.id.CustomIdentifierGenerator;
import cc.wdev.platform.commons.data.mybatis.log.MyBatisLog;
import cc.wdev.platform.commons.utils.CollectionUtils;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static cc.wdev.platform.commons.autoconfigure.data.properties.MyBatisCustomProperties.IDENTIFIER_GENERATOR_PREFIX;
import static cc.wdev.platform.commons.autoconfigure.data.properties.MyBatisCustomProperties.META_OBJECT_HANDLER_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration(after = {CoreAutoConfiguration.class})
@ConditionalOnClass({MybatisPlusAutoConfiguration.class})
@ConditionalOnProperty(prefix = MyBatisCustomProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MyBatisCustomProperties.class)
public class MyBatisCustomAutoConfiguration {

    private final MyBatisCustomProperties properties;

    private final GlobalContext context;

    public MyBatisCustomAutoConfiguration(MyBatisCustomProperties properties, GlobalContext context) {
        log.info("MyBatisCustomAutoConfiguration is enabled");

        this.properties = properties;
        this.context = context;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // MyBatis
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 数据库字典
     */
    @Bean("mybatisDatabaseIdProvider")
    @ConditionalOnMissingBean
    public DatabaseIdProvider databaseIdProvider() {
        Properties properties = new Properties();
        properties.put("PostgreSQL", "postgresql");
        properties.put("MySQL", "mysql");

        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // MyBatis-Plus
    // -----------------------------------------------------------------------------------------------------------------

    @Bean("mpIdentifierGenerator")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = IDENTIFIER_GENERATOR_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public IdentifierGenerator mpIdentifierGenerator() {
        return new CustomIdentifierGenerator();
    }

    @Bean("mpMetaObjectHandler")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = META_OBJECT_HANDLER_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public MetaObjectHandler mpMetaObjectHandler() {
        return new CustomMetaObjectHandler();
    }

    @Bean("mpTenantLineHandler")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CoreProperties.TENANCY_PREFIX, name = "enabled", havingValue = "true")
    public TenantLineHandler mpTenantLineHandler(ObjectProvider<List<TenantConfigCustomizer>> customizers) {
        TenantConfig config = TenantConfig.builder().build();
        // 从全局多租户配置中获取排除的表
        if (CollectionUtils.isNotEmpty(this.context.getTenancy().getExcludes())) {
            config.getExcludes().addAll(this.context.getTenancy().getExcludes());
        }
        // 从自定义配置中获取排除的表
        if (CollectionUtils.isNotEmpty(customizers.getIfAvailable())) {
            for (TenantConfigCustomizer customizer : customizers.getIfAvailable()) {
                customizer.customize(config);
            }
        }
        return new CustomTenantLineHandler(config.getExcludes());
    }

    /**
     * 增加自定义的Mapper XML文件路径
     */
    @Bean
    public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
        return properties -> properties.setMapperLocations(
            new String[]{"classpath*:/mapper/**/*.xml", "classpath*:/repository/**/*.xml"}
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(
        @Qualifier("mpTenantLineHandler") @Autowired(required = false) TenantLineHandler mpTenantLineHandler
    ) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户插件，需要放在第一位
        if (this.context.getTenancy().isEnabled() && mpTenantLineHandler == null) {
            log.info("Tenancy is enabled, but mpTenantLineHandler is null.");
        } else if (this.context.getTenancy().isEnabled()) {
            log.info("Tenancy is enabled, init TenantLineInnerInterceptor.");
            TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
            tenantInterceptor.setTenantLineHandler(mpTenantLineHandler);
            interceptor.addInnerInterceptor(tenantInterceptor);
        }
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // MyBatis Configuration Customizer
    // -----------------------------------------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer configurationCustomizer(
        DatabaseIdProvider databaseIdProvider,
        DataSource dataSource
    ) throws SQLException {
        String databaseId = databaseIdProvider.getDatabaseId(dataSource);

        log.info("Create MyBatis Configuration Customizer");
        log.info("MyBatis databaseId - {}", databaseId);
        log.info("MyBatis IdentifierGenerator - {}", this.properties.getIdentifierGenerator().isEnabled());
        log.info("MyBatis MetaObjectHandler - {}", this.properties.getMetaObjectHandler().isEnabled());
        log.info("MyBatis showSql - {}", this.properties.isShowSql());

        return configuration -> {
            if (this.properties.isShowSql()) {
                configuration.setLogImpl(MyBatisLog.class);
            } else {
                configuration.setLogImpl(NoLoggingImpl.class);
            }
            configuration.setDatabaseId(databaseId);
            configuration.setJdbcTypeForNull(JdbcType.NULL);
            configuration.setMapUnderscoreToCamelCase(true);
        };
    }

}
