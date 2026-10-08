package cc.wdev.platform.commons.annotations;

import cc.wdev.platform.commons.enums.RateLimitTypeEnum;

import java.lang.annotation.*;

/**
 * @author elvea
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RateLimiter {

    String key() default "";

    int time() default 60;

    int limit() default 100;

    RateLimitTypeEnum type() default RateLimitTypeEnum.DEFAULT;

    String message() default "{rate.limiter.message}";

}
