package cc.wdev.platform.commons.security.domain;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
@Getter
@Setter
public class User implements UserDetails, OAuth2AuthenticatedPrincipal, Serializable {

    private final Long id;

    private final Long tenantId;

    private final String password;

    private final String username;

    private final Set<GrantedAuthority> authorities;

    private final boolean enabled;

    private final boolean accountNonExpired;

    private final boolean accountNonLocked;

    private final boolean credentialsNonExpired;

    public User(Long id, String username, String password,
                Set<GrantedAuthority> authorities) {
        this(0L, id, username, password, authorities, true, true, true, true);
    }

    public User(Long tenantId, Long id, String username, String password, Set<GrantedAuthority> authorities) {
        this(tenantId, id, username, password, authorities, true, true, true, true);
    }

    public User(Long tenantId, Long id, String username, String password, Set<GrantedAuthority> authorities,
                boolean enabled, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired) {
        this.id = id;
        this.tenantId = tenantId;
        this.password = password;
        this.username = username;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Maps.newHashMap();
    }

    @Override
    public @NonNull String getName() {
        return this.getUsername();
    }

    public Set<String> getGrantedAuthority() {
        return this.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new User.AuthorityComparator());
        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            }
            if (g1.getAuthority() == null) {
                return 1;
            }
            return g1.getAuthority().compareTo(g2.getAuthority());
        }

    }

}
