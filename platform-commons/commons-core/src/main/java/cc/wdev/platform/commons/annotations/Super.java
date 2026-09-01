package cc.wdev.platform.commons.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@PreAuthorize("hasAnyAuthority(T(cc.wdev.platform.commons.constants.SecurityConstants).ROOT_AUTHORITY)")
public @interface Super {
}
