package cc.wdev.platform.security.core;

import cc.wdev.platform.commons.security.CustomParameterNames;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
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
public class OtpAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 一次性验证码类型
     */
    @Getter
    private final String otpType;

    /**
     * 邀请码
     */
    @Getter
    private final String inviteCode;

    /**
     * 手机国家区号
     */
    @Getter
    @Schema(description = "手机国家区号")
    private final String mobileCountryCode;

    /**
     * 手机
     */
    @Getter
    @Schema(description = "手机")
    private final String mobileNumber;

    /**
     * 电子邮箱
     */
    @Getter
    private final String email;

    @Getter
    private final String verifyCodeKey;

    @Getter
    private final String verifyCodeValue;

    @Setter
    private Object principal;

    @Setter
    private Object credentials;

    public OtpAuthenticationToken(String otpType,
                                  String mobileCountryCode, String mobileNumber, String email,
                                  String verifyCodeKey, String verifyCodeValue,
                                  Map<String, Object> parameters) {
        super(Collections.emptySet());

        // 基本参数
        this.otpType = otpType;
        this.mobileCountryCode = mobileCountryCode;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.verifyCodeKey = verifyCodeKey;
        this.verifyCodeValue = verifyCodeValue;

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
