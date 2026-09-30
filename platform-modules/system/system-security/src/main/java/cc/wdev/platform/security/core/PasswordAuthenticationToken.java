package cc.wdev.platform.security.core;

import cc.wdev.platform.commons.security.CustomParameterNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class PasswordAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final String inviteCode;

    private final Object principal;

    private Object credentials;

    public PasswordAuthenticationToken(Object principal, Object credentials, Map<String, Object> parameters) {
        super(Collections.emptySet());

        // 基本参数
        this.principal = principal;
        this.credentials = credentials;

        // 附加参数
        this.inviteCode = MapUtils.getString(parameters, CustomParameterNames.INVITE_CODE, "");

        setAuthenticated(false);
    }

    public static UsernamePasswordAuthenticationToken authenticated(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

}
