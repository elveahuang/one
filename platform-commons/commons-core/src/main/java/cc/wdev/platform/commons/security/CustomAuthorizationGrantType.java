package cc.wdev.platform.commons.security;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * @author elvea
 */
public class CustomAuthorizationGrantType {

    public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");

    public static final AuthorizationGrantType SOCIAL = new AuthorizationGrantType("social");

    public static final AuthorizationGrantType OTP = new AuthorizationGrantType("otp");

    public static final AuthorizationGrantType TYPE = new AuthorizationGrantType("type");

}
