package cc.wdev.platform.commons.core.cache.utils;

import cc.wdev.platform.commons.enums.RateLimitTypeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RTopic;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.time.Duration;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
public class RedissonUtils {

    private final RedissonClient client;

    public String getClientId() {
        return this.client.getId();
    }

    public long rateLimiter(String key, RateLimitTypeEnum type, long rate, long rateInterval) {
        RateType rateType = RateType.OVERALL;
        if (type == RateLimitTypeEnum.CLUSTER) {
            rateType = RateType.PER_CLIENT;
        }

        RRateLimiter rateLimiter = this.client.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, rate, Duration.ofSeconds(rateInterval));
        if (rateLimiter.tryAcquire()) {
            return rateLimiter.availablePermits();
        } else {
            return -1L;
        }
    }

    public RTopic getTopic(String name) {
        return this.client.getTopic(name);
    }

    public <T> void addListener(String name, Class<T> type, MessageListener<? extends T> listener) {
        RTopic topic = this.getTopic(name);
        topic.removeAllListeners();
        topic.addListener(type, listener);
    }

}
