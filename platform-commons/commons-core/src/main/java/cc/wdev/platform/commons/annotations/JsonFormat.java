package cc.wdev.platform.commons.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author elvea
 */
@com.fasterxml.jackson.annotation.JsonFormat
@JacksonAnnotation
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFormat {

    /**
     * 日期格式
     */
    @AliasFor(annotation = com.fasterxml.jackson.annotation.JsonFormat.class, attribute = "pattern")
    String pattern() default "";

    /**
     * 是否在转换时区，把用户提交的时间，按用户时区换算为系统时区的日期时间。
     */
    boolean timeZoneConvert() default false;

}
