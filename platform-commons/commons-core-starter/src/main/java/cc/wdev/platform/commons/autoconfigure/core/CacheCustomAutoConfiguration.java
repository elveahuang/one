package cc.wdev.platform.commons.autoconfigure.core;

import cc.wdev.platform.commons.autoconfigure.core.properties.CacheCustomProperties;
import cc.wdev.platform.commons.core.cache.CacheServiceManager;
import cc.wdev.platform.commons.core.cache.NullValue;
import cc.wdev.platform.commons.core.cache.aspect.RateLimitAspect;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.core.cache.service.RedissonCacheService;
import cc.wdev.platform.commons.core.cache.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.ForyCodec;
import org.redisson.codec.JsonJackson3Codec;
import org.redisson.codec.SerializationCodec;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.StringUtils;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.RedisClient;

import java.util.function.Consumer;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({RedissonClient.class})
@EnableConfigurationProperties({CacheCustomProperties.class, DataRedisProperties.class})
@ConditionalOnProperty(prefix = CacheCustomProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@ImportRuntimeHints(CacheCustomAutoConfiguration.CacheRuntimeHints.class)
public class CacheCustomAutoConfiguration {

    private final CacheCustomProperties properties;

    public CacheCustomAutoConfiguration(CacheCustomProperties properties) {
        log.info("CacheCustomAutoConfiguration is enabled");

        this.properties = properties;
    }

    /**
     * 创建一个基于jedis的RedisClient示例
     * 1. 向量数据库
     * 2. RediSearch
     */
    @Bean
    @ConditionalOnMissingBean(RedisClient.class)
    @ConditionalOnClass(RedisClient.class)
    public RedisClient redisClient(DataRedisProperties redisProperties) {
        log.info("Creating RedisClient");

        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        builder.ssl(redisProperties.getSsl().isEnabled());
        if (StringUtils.hasText(redisProperties.getClientName())) {
            builder.clientName(redisProperties.getClientName());
        }
        if (redisProperties.getTimeout() != null) {
            builder.timeoutMillis((int) redisProperties.getTimeout().toMillis());
        }
        if (StringUtils.hasText(redisProperties.getUsername())) {
            builder.user(redisProperties.getUsername());
        }
        if (StringUtils.hasText(redisProperties.getPassword())) {
            builder.password(redisProperties.getPassword());
        }
        builder.database(redisProperties.getDatabase());

        return RedisClient.builder()
            .hostAndPort(redisProperties.getHost(), redisProperties.getPort())
            .clientConfig(builder.build())
            .build();
    }

    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer() {
        Codec codec = switch (this.properties.getCodec()) {
            case JDK -> new SerializationCodec();
            case FORY -> new ForyCodec();
            default -> new JsonJackson3Codec();
        };
        return config -> config.setCodec(codec);
    }

    @Bean
    @ConditionalOnMissingBean(RedissonUtils.class)
    public RedissonUtils redissonUtils(RedissonClient redissonClient) {
        log.info("Creating RedissonUtils");
        return new RedissonUtils(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean(RedissonCacheService.class)
    public CacheService cacheService(RedissonClient redissonClient, RedissonUtils redissonUtils) {
        log.info("Creating CacheService");
        CacheService cacheService = new RedissonCacheService(redissonClient, redissonUtils,
            this.properties.isCacheNullValue(), this.properties.getBatchSize());
        log.info("Initialize CacheServiceManager");
        CacheServiceManager.setCacheService(cacheService);
        return cacheService;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(CacheService.class)
    public RateLimitAspect rateLimitAspect(CacheService cacheService) {
        log.info("Creating RateLimitAspect");
        return new RateLimitAspect(cacheService);
    }

    @Primary
    @ConditionalOnMissingBean
    @Bean(name = "cacheManager")
    @ConditionalOnProperty(prefix = CacheCustomProperties.PREFIX, name = "provider", havingValue = "spring")
    public CacheManager redisCacheManager(RedissonConnectionFactory connectionFactory) {
        log.info("Creating RedisCacheManager");

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(this.properties.getTimeToLive())
            .enableTimeToIdle();
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }

    @Primary
    @ConditionalOnMissingBean
    @Bean(name = "cacheManager")
    @ConditionalOnProperty(prefix = CacheCustomProperties.PREFIX, name = "provider", havingValue = "redisson", matchIfMissing = true)
    public CacheManager redissonSpringCacheManager(RedissonClient redissonClient) {
        log.info("Creating RedissonSpringCacheManager");
        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redissonClient);
        cacheManager.setAllowNullValues(this.properties.isCacheNullValue());
        return cacheManager;
    }

    static class CacheRuntimeHints implements RuntimeHintsRegistrar {

        private static final Consumer<TypeHint.Builder> INVOKE_DECLARED_CONSTRUCTORS = TypeHint.builtWith(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(NullValue.class, INVOKE_DECLARED_CONSTRUCTORS);
        }

    }

}
