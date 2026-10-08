package cc.wdev.platform.system.core.api;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.system.core.domain.dto.UserAuthorityDto;
import cc.wdev.platform.system.core.domain.dto.UserInfoDto;
import cc.wdev.platform.system.core.domain.dto.UserLoginDto;
import cc.wdev.platform.system.core.domain.form.*;
import cc.wdev.platform.system.core.domain.request.*;
import cc.wdev.platform.system.core.domain.vo.UserForgetPasswordVo;
import cc.wdev.platform.system.core.domain.vo.UserInfoVo;
import cc.wdev.platform.system.core.domain.vo.UserSimpleInfoVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/user")
public interface UserApi {

    /**
     * 搜索用户
     */
    Page<UserSimpleInfoVo> search(UserSearchRequest request);

    /**
     * 获取用户分页列表
     */
    Page<UserInfoDto> findUserPage(UserSearchRequest request);

    /**
     * 批量获取用户
     */
    List<UserSimpleInfoVo> findUserList(List<Long> ids);

    /**
     * 检查用户名，邮箱或者手机号码是否可用
     */
    boolean check(UserCheckRequest request);

    /**
     * 用户注册
     */
    R<?> register(UserRegisterForm userRegisterForm);

    /**
     * 获取用户详情
     */
    UserInfoDto findUserDetails(Long id);

    /**
     * 获取用户信息
     */
    UserInfoDto getUserInfo(Long id);

    /**
     * 获取登录用户信息
     */
    UserInfoDto getUserInfo(String username);

    /**
     * 根据用户名查找账号
     */
    UserInfoDto getBaseUserInfo(String username);

    /**
     * 根据用户ID查找账号
     */
    UserInfoDto getBaseUserInfo(Long userId);

    /**
     * 批量获取用户基本信息
     */
    Map<Long, UserInfoVo> batchUserInfo(Collection<Long> userIds);

    /**
     * 根据用户名查找账号
     */
    @GetExchange("/find-by-username")
    UserLoginDto findByUsername(@Parameter(description = "用户名") @RequestParam("username") String username);

    /**
     * 根据手机号码查找用户
     */
    @GetExchange("/find-by-mobile")
    UserLoginDto findByMobile(@Parameter(description = "手机国家区号") @RequestParam("mobileCountryCode") String mobileCountryCode,
                              @Parameter(description = "手机号码") @RequestParam("mobileNumber") String mobileNumber);

    /**
     * 根据邮箱查找用户
     */
    @GetExchange("/find-by-email")
    UserLoginDto findByEmail(@Parameter(description = "邮箱") @RequestParam("email") String email);

    /**
     * 社区登录认证获取账号
     */
    @PostExchange("/find-by-social")
    UserLoginDto findBySocial(@RequestBody SocialUser socialUser);

    /**
     * 注册社区账号
     */
    @PostExchange("/register-social-user")
    UserLoginDto registerSocialUser(@RequestBody SocialUser socialUser);

    /**
     * OTP登录认证获取账号
     */
    @PostExchange("/find-by-otp")
    UserLoginDto findByOtp(@RequestBody OtpUser otpUser);

    /**
     * 注册手机|邮箱账号
     */
    @PostExchange("/register-otp-user")
    UserLoginDto registerOtpUser(@RequestBody OtpUser smsUser);

    /**
     * 获取权限列表
     */
    @GetExchange("/get-security-authority")
    UserAuthorityDto getUserAuthority(@Parameter(description = "用户ID") @RequestParam("id") Long id);

    /**
     * 修改账号个人信息
     */
    R<?> updateAccount(UserAccountForm userAccountForm);

    /**
     * 修改用户密码
     */
    R<?> resetPassword(ResetPasswordForm userRegisterForm);

    /**
     * 忘记密码，根据邮箱获取账号的邮箱地址和用户名
     */
    R<UserForgetPasswordVo> forgotPassword(ForgotPasswordForm userRegisterForm);

    /**
     * 退出登录
     */
    R<?> logout();

    /**
     * 保存用户
     */
    void saveUser(UserForm form);

    /**
     * 删除用户
     */
    void deleteUser(Collection<Long> ids);

    /**
     * 修改个人基本信息
     */
    void changeInfo(UserChangeInfoRequest request);

    /**
     * 获取二维码
     */
    R<?> getQRCode(String targetPath);

    /**
     * 获取时间段内全站的注册人数
     */
    long getRegisterCount(@NonNull UserRegisterCountRequest request);

    /**
     * 查询用户数
     */
    long getCount(@NonNull UserCountRequest request);

    //========================================================================================================================================================//

    /**
     * 检查原密码是否匹配
     */
    Boolean checkOriPassword(UserOriPasswordCheckRequest request);

    /**
     * 修改账号密码
     */
    void changePassword(UserChangePasswordForm changePasswordForm);

    /**
     * 修改用户名
     */
    void changeUserName(UserChangeUserNameForm form);

    /**
     * 修改个人邮箱
     */
    void changeEmail(UserChangeEmailForm form);

    /**
     * 修改个人手机号
     */
    void changePhone(UserChangePhoneForm form);

    /**
     * 获取单位时间所有注册用户数
     */
    long getAllRegisterCount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取单位时间前所有用户数
     */
    long getAllUserCount(LocalDateTime endTime);

    /**
     * 获取单位时间租户注册用户数
     */
    long getRegisterCountByTenantId(LocalDateTime startTime, LocalDateTime endTime, long tenantId);

    /**
     * 获取单位时间推荐注册人数
     */
    long getInviteRegisterCount(LocalDateTime startTime, LocalDateTime endTime);

}
