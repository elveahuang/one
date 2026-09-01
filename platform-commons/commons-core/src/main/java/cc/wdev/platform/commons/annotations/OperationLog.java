package cc.wdev.platform.commons.annotations;

import java.lang.annotation.*;

/**
 * 日志注解
 *
 * @author elvea
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    String module() default "";

    String value() default "";

    boolean request() default true;

    boolean response() default true;

    /**
     * 排除某些敏感字段，比如密码等信息，不宜直接保存到日志文件
     */
    String[] excludes() default {};

}
