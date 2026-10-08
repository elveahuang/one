package cc.wdev.platform.security.core.service;

import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.commons.security.domain.SocialUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author elvea
 */
public interface BaseUserDetailsService extends UserDetailsService {

    UserDetails loadUserBySocial(SocialUser socialUser) throws Exception;

    UserDetails loadUserByOtp(OtpUser smsUser) throws Exception;

}
