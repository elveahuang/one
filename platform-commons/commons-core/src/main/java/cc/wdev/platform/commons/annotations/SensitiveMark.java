package cc.wdev.platform.commons.annotations;

import cc.wdev.platform.commons.extensions.sensitive.mark.SensitiveMarkSerializer;
import cc.wdev.platform.commons.extensions.sensitive.mark.SensitiveMarkStrategy;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import tools.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author elvea
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveMarkSerializer.class)
public @interface SensitiveMark {

    SensitiveMarkStrategy strategy();

}
