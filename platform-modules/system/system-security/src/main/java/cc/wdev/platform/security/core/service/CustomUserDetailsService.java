package cc.wdev.platform.security.core.service;

import cc.wdev.platform.commons.enums.MobileCountryCodeTypeEnum;
import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.commons.security.domain.User;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.RegexUtils;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.domain.dto.UserLoginDto;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * @author elvea
 * @see UserDetailsService
 */
@Slf4j
@Primary
@Service
@RequiredArgsConstructor
@Qualifier("userDetailsService")
public class CustomUserDetailsService implements BaseUserDetailsService {

    private final UserApi userApi;

    /**
     * @see UserDetailsService#loadUserByUsername(String)
     */
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserLoginDto user;
        if (RegexUtils.checkEmail(username)) {
            log.info("findByEmail by [{}]", username);
            user = userApi.findByEmail(username);
        } else if (RegexUtils.checkMobile(username)) {
            log.info("findByMobile by [{}]", username);
            user = userApi.findByMobile(MobileCountryCodeTypeEnum.ZH_CN.getValue(), username);
        } else {
            log.info("findByUsername [{}]", username);
            user = userApi.findByUsername(username);
        }
        if (null == user) {
            throw new UsernameNotFoundException(username);
        }

        log.info("loadUserByUsername by [{}]. retrieved.", username);

        Set<GrantedAuthority> authorities = CollectionUtils.isNotEmpty(user.getGrantedAuthorities()) ? user.getGrantedAuthorities() : Sets.newHashSet();
        return new User(user.getTenantId(), user.getId(), user.getUsername(), user.getPassword(), authorities);
    }

    /**
     * @see BaseUserDetailsService#loadUserBySocial(SocialUser)
     */
    public UserDetails loadUserBySocial(SocialUser socialUser) throws Exception {
        UserLoginDto user = userApi.findBySocial(socialUser);
        if (null == user) {
            user = userApi.registerSocialUser(socialUser);
        }
        if (null == user) {
            throw new UsernameNotFoundException(socialUser.getOpenId());
        }
        return new User(user.getTenantId(), user.getId(), user.getUsername(), user.getPassword(), Collections.emptySet());
    }

    /**
     * @see BaseUserDetailsService#loadUserByOtp(OtpUser)
     */
    @Override
    public UserDetails loadUserByOtp(OtpUser otpUser) throws Exception {
        UserLoginDto user = userApi.findByOtp(otpUser);
        if (null == user) {
            user = userApi.registerOtpUser(otpUser);
        }
        if (null == user) {
            throw new UsernameNotFoundException("User not found.");
        }
        return this.loadUserByUsername(user.getUsername());
    }

}
