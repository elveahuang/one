package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.constants.SecurityConstants;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.security.domain.User;
import cc.wdev.platform.commons.utils.jwt.JwtService;
import com.google.common.collect.Sets;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
@Slf4j
public abstract class SecurityUtils {

    public static PasswordEncoder encoder;

    static {
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        if (encoder instanceof DelegatingPasswordEncoder delegatingPasswordEncoder) {
            delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        }
    }

    public static String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotEmpty(header) && header.startsWith("Bearer ")) {
            return header.split(" ")[1].trim();
        }
        return null;
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户ID
     */
    public static User getUser(Authentication authentication) {
        if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof User user) {
            return user;
        } else if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof Jwt jwt) {
            Long uid = jwt.getClaim(SecurityConstants.JWT_KEY_UID);
            Long tid = jwt.getClaim(SecurityConstants.JWT_KEY_TID);
            String username = jwt.getClaimAsString(SecurityConstants.JWT_KEY_USERNAME);
            Set<GrantedAuthority> authorities = Sets.newHashSet(authentication.getAuthorities());
            return new User(tid, uid, username, null, authorities);
        } else if (authentication instanceof OAuth2AccessTokenAuthenticationToken token) {
            try {
                JwtService jwtService = SpringUtils.getBean(JwtService.class);
                Jwt jwt = jwtService.parseJwtToken(token.getAccessToken().getTokenValue());
                Long uid = jwt.getClaim(SecurityConstants.JWT_KEY_UID);
                Long tid = jwt.getClaim(SecurityConstants.JWT_KEY_TID);
                String username = jwt.getClaimAsString(SecurityConstants.JWT_KEY_USERNAME);
                Set<GrantedAuthority> authorities = Sets.newHashSet(token.getAuthorities());
                return new User(tid, uid, username, null, authorities);
            } catch (Exception e) {
                log.warn("Failed to extract user from OAuth2AccessTokenAuthenticationToken", e);
            }
        }
        return null;
    }

    /**
     * 获取用户ID
     */
    public static User getUser() {
        return getUser(getAuthentication());
    }

    /**
     * 获取会话ID
     */
    public static String getSid() {
        return getSid(getAuthentication());
    }

    /**
     * 获取租户ID
     */
    public static Long getTid() {
        return getTid(getAuthentication());
    }

    /**
     * 获取会话ID
     */
    public static String getSid(Authentication authentication) {
        if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getId();
        }
        return null;
    }

    /**
     * 获取用户ID
     */
    public static Long getUid(Authentication authentication) {
        if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof User user) {
            return user.getId();
        } else if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim(SecurityConstants.JWT_KEY_UID);
        }
        return 0L;
    }

    /**
     * 获取用户ID
     */
    public static Long getUid() {
        return getUid(getAuthentication());
    }

    /**
     * 根据前缀获取权限
     */
    public static Set<String> getAuthorities(Authentication authentication, String prefix) {
        if (null == authentication || CollectionUtils.isEmpty(authentication.getAuthorities())) {
            return Collections.emptySet();
        }
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(auth -> null != auth && auth.startsWith(prefix))
            .map(auth -> auth.substring(prefix.length()))
            .collect(Collectors.toSet());
    }

    /**
     * 获取用户所有数据范围
     */
    public static Set<String> getDataScopes() {
        return getAuthorities(getAuthentication(), SecurityConstants.DATA_SCOPE_PREFIX);
    }

    /**
     * 判断用户数据范围
     */
    public static boolean hasDataScope(String dataScope) {
        if (StringUtils.isEmpty(dataScope)) {
            return Boolean.FALSE;
        }
        return getDataScopes().contains(dataScope);
    }

    /**
     * 获取用户所有权限
     */
    public static Set<String> getRoleTypes() {
        return getAuthorities(getAuthentication(), SecurityConstants.ROLE_PREFIX);
    }

    /**
     * 判断用户权限
     */
    public static boolean hasRoleType(String roleType) {
        if (StringUtils.isEmpty(roleType)) {
            return Boolean.FALSE;
        }
        return getRoleTypes().contains(roleType);
    }

    /**
     * 获取用户名
     */
    public static String getUsername(Authentication authentication) {
        if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof User user) {
            return user.getUsername();
        } else if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim(SecurityConstants.JWT_KEY_USERNAME);
        } else if (!ObjectUtils.isEmpty(authentication)) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取租户ID
     */
    public static Long getTid(Authentication authentication) {
        if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof User user) {
            return user.getTenantId();
        } else if (!ObjectUtils.isEmpty(authentication) && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim(SecurityConstants.JWT_KEY_TID);
        }
        return TenantContext.getTenantId();
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return getUsername(getAuthentication());
    }

    /**
     * 是否为系统管理员
     */
    public static boolean isAdmin() {
        return isAdmin(getUid());
    }

    /**
     * 是否为系统管理员
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 未登陆则表明是匿名用户
     *
     * @return boolean
     */
    public static boolean isAnonymous() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || authentication.getPrincipal() == null ||
            "anonymousUser".equals(authentication.getPrincipal()) || !authentication.isAuthenticated();
    }

    /**
     * 当前是否已经登录
     *
     * @return boolean
     */
    public static boolean isAuthenticated() {
        return !Objects.isNull(SecurityContextHolder.getContext().getAuthentication())
            && SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    /**
     * 密码加密
     *
     * @param password 明文密码
     * @return 加密密码
     */
    public static String encode(String password) {
        return getPasswordEncoder().encode(password);
    }

    /**
     * 密码比对
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 明文密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return getPasswordEncoder().matches(rawPassword, encodedPassword);
    }

    /**
     * 获取密码编码器
     */
    public static PasswordEncoder getPasswordEncoder() {
        return encoder;
    }

    /**
     * 权限排序
     */
    public static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
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
