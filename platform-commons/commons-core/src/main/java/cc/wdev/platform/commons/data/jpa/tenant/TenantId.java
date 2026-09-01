package cc.wdev.platform.commons.data.jpa.tenant;

import org.hibernate.annotations.AttributeBinderType;
import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ValueGenerationType(generatedBy = TenantIdGeneration.class)
@AttributeBinderType(binder = TenantIdBinder.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface TenantId {
}
