package cc.wdev.platform.commons.annotations;

import cc.wdev.platform.commons.extensions.sensitive.encrypt.SensitiveEncryptStrategy;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

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
public @interface SensitiveEncrypt {

    SensitiveEncryptStrategy strategy();

}
