package cc.wdev.platform.commons.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author elvea
 */
@org.springframework.format.annotation.DateTimeFormat
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface DateTimeFormat {

    /**
     * 日期格式
     */
    @AliasFor(annotation = org.springframework.format.annotation.DateTimeFormat.class, attribute = "pattern")
    String pattern() default "";

    /**
     * 是否在转换时区，把用户提交的时间，按用户时区换算为系统时区的日期时间。
     */
    boolean timeZoneConvert() default false;

}
