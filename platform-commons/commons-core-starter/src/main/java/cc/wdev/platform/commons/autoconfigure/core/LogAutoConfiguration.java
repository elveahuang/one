package cc.wdev.platform.commons.autoconfigure.core;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.autoconfigure.core.properties.LogProperties;
import cc.wdev.platform.commons.autoconfigure.core.properties.WebProperties;
import cc.wdev.platform.commons.core.log.aspect.OperationLogAspect;
import cc.wdev.platform.commons.core.log.config.LogConfig;
import cc.wdev.platform.commons.core.log.config.LogConfigCustomizer;
import cc.wdev.platform.commons.core.log.domain.OperationLogDto;
import cc.wdev.platform.commons.core.log.domain.UrlLogDto;
import cc.wdev.platform.commons.core.log.store.DefaultLogStore;
import cc.wdev.platform.commons.core.log.store.LogStore;
import cc.wdev.platform.commons.extensions.captcha.domain.CaptchaLogDto;
import cc.wdev.platform.commons.web.servlet.interceptor.LogInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author elvea
 */
@Slf4j
@EnableAspectJAutoProxy
@AutoConfiguration(after = CoreAutoConfiguration.class)
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(prefix = LogProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@ImportRuntimeHints(LogAutoConfiguration.LogRuntimeHints.class)
@RegisterReflectionForBinding({UrlLogDto.class, OperationLogDto.class, CaptchaLogDto.class})
public class LogAutoConfiguration {

    private final LogProperties properties;

    public LogAutoConfiguration(LogProperties properties) {
        log.info("LogAutoConfiguration is enabled");
        this.properties = properties;
    }

    /**
     * 日志配置
     */
    @Bean
    @ConditionalOnMissingBean
    public LogConfig logConfig(ObjectProvider<List<LogConfigCustomizer>> customizers) {
        LogConfig config = this.properties.getConfig();
        if (this.properties.getConfig() == null) {
            config = LogConfig.builder().build();
        }
        if (config != null && !CollectionUtils.isEmpty(customizers.getIfAvailable())) {
            for (LogConfigCustomizer customizer : customizers.getIfAvailable()) {
                customizer.customize(config);
            }
        }
        return this.properties.getConfig();
    }

    /**
     * 日志存储
     */
    @Bean
    @ConditionalOnMissingBean
    public LogStore logStore(LogConfig config) {
        return new DefaultLogStore(config);
    }

    /**
     * 操作日志切面，用于拦截标注了{@link OperationLog}注解的方法
     */
    @Bean
    @ConditionalOnMissingBean
    public OperationLogAspect operationLogAspect(LogStore store, LogConfig config) {
        log.info("Creating OperationLogAspect");
        return new OperationLogAspect(store, config);
    }

    /**
     * 仅在Servlet环境下才会启用，创建拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass({HttpServletRequest.class})
    @ConditionalOnProperty(prefix = WebProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public LogInterceptor logInterceptor(LogStore store, LogConfig config) {
        log.info("Creating LogInterceptor");
        return new LogInterceptor(store, config);
    }

    // ------------------------------------------------------------------------------
    // AOT
    // ------------------------------------------------------------------------------

    public static class LogRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
            log.info("Register RuntimeHints by LogRuntimeHints");

            var mcs = MemberCategory.values();
            for (var type : Set.of(UrlLogDto.class, OperationLogDto.class, CaptchaLogDto.class)) {
                hints.reflection().registerType(type, mcs);
            }
        }
    }

}
