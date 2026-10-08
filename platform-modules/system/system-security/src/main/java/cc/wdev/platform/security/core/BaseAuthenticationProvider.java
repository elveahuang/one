package cc.wdev.platform.security.core;

import cc.wdev.platform.security.core.service.BaseUserDetailsService;
import cc.wdev.platform.security.core.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;

/**
 * @author elvea
 */
@Slf4j
public abstract class BaseAuthenticationProvider implements AuthenticationProvider {

    protected final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    protected final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    protected final CustomUserDetailsService userDetailsService;

    public BaseAuthenticationProvider(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected BaseUserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

}
