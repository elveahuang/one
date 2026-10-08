package cc.wdev.platform.commons.data.jpa.id;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(CustomIdentifierGenerator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Sequence {
}
