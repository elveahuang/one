package cc.wdev.platform.security.core;

import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.security.core.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * @author elvea
 */
@Slf4j
public class PasswordAuthenticationProvider extends BaseAuthenticationProvider {

    public PasswordAuthenticationProvider(CustomUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return (PasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(PasswordAuthenticationToken.class, authentication, () ->
            this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports", "Only PasswordAuthenticationToken is supported"));

        PasswordAuthenticationToken authenticationToken = (PasswordAuthenticationToken) authentication;

        UserDetails user;

        // 匹配本地用户
        String identify = determinePrincipal(authentication);
        try {
            log.info("Retrieve social user '{}'", identify);
            user = this.retrieveUser(identify, authenticationToken);
        } catch (UsernameNotFoundException ex) {
            log.info("Failed to find user '{}'", identify);
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        additionalAuthenticationChecks(user, authenticationToken);
        Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");

        return createSuccessAuthentication(user, authentication, user);
    }

    private String determinePrincipal(Authentication authentication) {
        return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, PasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.info("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!SecurityUtils.matches(presentedPassword, userDetails.getPassword())) {
            log.info("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    protected UserDetails retrieveUser(String identify, PasswordAuthenticationToken authentication) throws AuthenticationException {
        try {
            return this.getUserDetailsService().loadUserByUsername(identify);
        } catch (UsernameNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        UsernamePasswordAuthenticationToken result = PasswordAuthenticationToken.authenticated(
            principal, authentication.getCredentials(), this.authoritiesMapper.mapAuthorities(user.getAuthorities())
        );
        result.setDetails(authentication.getDetails());
        return result;
    }

}
