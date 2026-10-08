package cc.wdev.platform.system.core.controller.exchange;

import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.domain.dto.UserAuthorityDto;
import cc.wdev.platform.system.core.domain.dto.UserLoginDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserExchangeController {

    private final UserApi userApi;

    /**
     * 根据用户名查找账号
     */
    @GetMapping(EXCHANGE_PREFIX + "/user/find-by-username")
    public UserLoginDto findByUsername(@Parameter(description = "用户名") @RequestParam("username") String username) {
        return this.userApi.findByUsername(username);
    }

    /**
     * 获取权限列表
     */
    @GetMapping(EXCHANGE_PREFIX + "/user/get-security-authority")
    public UserAuthorityDto getSecurityAuthority(@Parameter(description = "用户ID") @RequestParam("id") Long id) {
        return this.userApi.getUserAuthority(id);
    }

    /**
     * 根据手机号码查找用户
     */
    @GetMapping(EXCHANGE_PREFIX + "/user/find-by-mobile")
    public UserLoginDto findByMobile(@Parameter(description = "手机国家区号") @RequestParam("mobileCountryCode") String mobileCountryCode,
                                     @Parameter(description = "手机号码") @RequestParam("mobileNumber") String mobileNumber) {
        return this.userApi.findByMobile(mobileCountryCode, mobileNumber);
    }

    /**
     * 根据邮箱查找用户
     */
    @GetMapping(EXCHANGE_PREFIX + "/user/find-by-email")
    public UserLoginDto findByEmail(@Parameter(description = "邮箱") @RequestParam("email") String email) {
        return this.userApi.findByEmail(email);
    }

    /**
     * 社区登录认证获取账号
     */
    @PostMapping(EXCHANGE_PREFIX + "/user/find-by-social")
    public UserLoginDto findBySocial(@RequestBody SocialUser socialUser) {
        return this.userApi.findBySocial(socialUser);
    }

    /**
     * 注册社区账号
     */
    @PostMapping(EXCHANGE_PREFIX + "/user/register-social-user")
    public UserLoginDto registerSocialUser(@RequestBody SocialUser socialUser) throws Exception {
        return this.userApi.registerSocialUser(socialUser);
    }

    /**
     * OTP登录认证获取账号
     */
    @PostMapping(EXCHANGE_PREFIX + "/user/find-by-otp")
    public UserLoginDto findByOtp(@RequestBody OtpUser otpUser) {
        return this.userApi.findByOtp(otpUser);
    }

    /**
     * 注册手机|邮箱账号
     */
    @PostMapping(EXCHANGE_PREFIX + "/user/register-otp-user")
    public UserLoginDto registerOtpUser(@RequestBody OtpUser smsUser) throws Exception {
        return this.userApi.registerOtpUser(smsUser);
    }

}
