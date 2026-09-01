package cc.wdev.platform.security.core;

import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.security.core.service.CustomUserDetailsService;
import cc.wdev.platform.system.commons.api.SocialApi;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class SocialAuthenticationProvider extends BaseAuthenticationProvider {

    private SocialApi socialApi;

    public SocialAuthenticationProvider(CustomUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return (SocialAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(SocialAuthenticationToken.class, authentication, () ->
            this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports", "Only SocialAuthenticationToken is supported"));

        SocialAuthenticationToken authenticationToken = (SocialAuthenticationToken) authentication;

        UserDetails user;

        // 匹配本地用户
        SocialUser socialUser = this.determinePrincipal(authenticationToken);
        try {
            log.info("Retrieve social user '{}'", socialUser.getPrincipal());
            user = this.retrieveUser(socialUser, authenticationToken);
        } catch (UsernameNotFoundException ex) {
            log.info("Failed to find social user '{}'", socialUser.getPrincipal());
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");

        return createSuccessAuthentication(user, authentication, user);
    }

    protected SocialUser determinePrincipal(SocialAuthenticationToken authenticationToken) {
        Map<String, Object> parameters = authenticationToken.getParameters();
        return this.socialApi.retrieveSocialUser(authenticationToken.getSocialType(), parameters);
    }

    protected UserDetails retrieveUser(SocialUser socialUser, SocialAuthenticationToken authentication) throws AuthenticationException {
        try {
            // 获取用户
            return this.getUserDetailsService().loadUserBySocial(socialUser);
        } catch (UsernameNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        UsernamePasswordAuthenticationToken result = SocialAuthenticationToken.authenticated(
            principal, authentication.getCredentials(), this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Autowired
    public void setSocialApi(SocialApi socialApi) {
        this.socialApi = socialApi;
    }

}
