package cc.wdev.platform.system.core.controller.webapp;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.annotations.RateLimiter;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.domain.dto.UserInfoDto;
import cc.wdev.platform.system.core.domain.form.*;
import cc.wdev.platform.system.core.domain.request.UserChangeInfoRequest;
import cc.wdev.platform.system.core.domain.request.UserCheckRequest;
import cc.wdev.platform.system.core.domain.request.UserOriPasswordCheckRequest;
import cc.wdev.platform.system.core.domain.vo.UserForgetPasswordVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.commons.enums.RateLimitTypeEnum.IP;
import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "UserController", description = "用户控制器")
public class UserWebController {

    private final UserApi userApi;

    @Anonymous
    @Operation(summary = "检查用户名是否可用")
    @ApiResponse(description = "检查用户名是否可用")
    @GetMapping(API_V1_PREFIX + "/user/check-username")
    @RateLimiter(type = IP)
    public R<Boolean> checkUsername(@Valid @Parameter(description = "用户检查请求") UserCheckRequest request) {
        return R.success(userApi.check(request));
    }

    @Anonymous
    @Operation(summary = "检查邮箱是否可用")
    @ApiResponse(description = "检查邮箱是否可用")
    @GetMapping(API_V1_PREFIX + "/user/check-email")
    @RateLimiter(type = IP)
    public R<Boolean> checkEmail(@Valid @Parameter(description = "用户检查请求") UserCheckRequest request) {
        return R.success(userApi.check(request));
    }

    @Anonymous
    @Operation(summary = "检查手机号码是否可用")
    @ApiResponse(description = "检查手机号码是否可用")
    @GetMapping(API_V1_PREFIX + "/user/check-mobile")
    @RateLimiter(type = IP)
    public R<Boolean> checkMobile(@Valid @Parameter(description = "用户检查请求") UserCheckRequest request) {
        return R.success(userApi.check(request));
    }

    @Authenticated
    @Operation(summary = "获取个人用户信息")
    @ApiResponse(description = "获取个人用户信息")
    @GetMapping(API_V1_PREFIX + "/user")
    public R<UserInfoDto> userInfo() {
        return R.success(this.userApi.getUserInfo(SecurityUtils.getUsername()));
    }

    @Authenticated
    @Operation(summary = "获取当前用户详情")
    @ApiResponse(description = "获取当前用户详情")
    @GetMapping(API_V1_PREFIX + "/user/details")
    public R<UserInfoDto> baseUser() {
        return R.success(this.userApi.getBaseUserInfo(SecurityUtils.getUsername()));
    }

    @Authenticated
    @Operation(summary = "获取当前用户详情")
    @ApiResponse(description = "获取当前用户详情")
    @GetMapping(API_V1_PREFIX + "/user/info")
    public R<UserInfoDto> info(@Parameter(description = "用户ID") @RequestParam(value = "userId") Long userId) {
        return R.success(this.userApi.getBaseUserInfo(userId));
    }

    @Anonymous
    @OperationLog("用户注册")
    @Operation(summary = "用户注册")
    @ApiResponse(description = "用户注册")
    @PostMapping(API_V1_PREFIX + "/user/register")
    public R<?> register(@Parameter(description = "用户注册表单") @Valid UserRegisterForm form) {
        return userApi.register(form);
    }

    @Anonymous
    @OperationLog("退出登录")
    @Operation(summary = "退出登录")
    @ApiResponse(description = "退出登录")
    @PostMapping(API_V1_PREFIX + "/user/logout")
    public R<?> logout() {
        return userApi.logout();
    }

    @Anonymous
    @OperationLog("忘记密码")
    @Operation(summary = "忘记密码")
    @ApiResponse(description = "忘记密码")
    @PostMapping(API_V1_PREFIX + "/user/forgot-password")
    public R<UserForgetPasswordVo> forgotPassword(@Parameter(description = "忘记密码表单") @Valid ForgotPasswordForm form) {
        return userApi.forgotPassword(form);
    }

    @Anonymous
    @Operation(summary = "重置密码")
    @ApiResponse(description = "重置密码")
    @PostMapping(API_V1_PREFIX + "/user/reset-password")
    public R<?> resetPassword(@Parameter(description = "重置密码表单") @Valid ResetPasswordForm form) {
        return userApi.resetPassword(form);
    }

    @Authenticated
    @OperationLog("修改邮箱")
    @Operation(summary = "修改邮箱")
    @ApiResponse(description = "修改邮箱")
    @PostMapping(API_V1_PREFIX + "/user/change-email")
    public R<?> changeEmail(@Parameter(description = "修改邮箱表单") @Valid UserChangeEmailForm form) throws Exception {
        userApi.changeEmail(form);
        return R.success();
    }

    @Authenticated
    @Operation(summary = "编辑个人资料")
    @ApiResponse(description = "编辑个人资料")
    @OperationLog("编辑个人资料")
    @PostMapping(API_V1_PREFIX + "/user/save")
    public R<?> updateAccount(@Parameter(description = "编辑个人资料表单") @RequestBody @Valid UserAccountForm form) throws Exception {
        return userApi.updateAccount(form);
    }

    @Authenticated
    @Operation(summary = "获取邀请二维码")
    @ApiResponse(description = "获取邀请二维码")
    @GetMapping(API_V1_PREFIX + "/user/qr-code")
    public R<?> qrCode(@Parameter(description = "目标路径") @RequestParam(value = "targetPath") String targetPath) {
        return userApi.getQRCode(targetPath);
    }

    @Authenticated
    @OperationLog("修改用户名")
    @Operation(summary = "修改用户名")
    @ApiResponse(description = "修改用户名")
    @PostMapping(API_V1_PREFIX + "/user/change-username")
    public R<?> changeUserName(@Parameter(description = "修改用户名表单") @Valid UserChangeUserNameForm form) throws Exception {
        this.userApi.changeUserName(form);
        return R.success();
    }

    @Authenticated
    @OperationLog("修改手机号")
    @Operation(summary = "修改手机号")
    @ApiResponse(description = "修改手机号")
    @PostMapping(API_V1_PREFIX + "/user/change-phone")
    public R<?> changePhone(@Parameter(description = "修改手机号表单") @Valid UserChangePhoneForm form) throws Exception {
        this.userApi.changePhone(form);
        return R.success();
    }

    @Anonymous
    @Operation(summary = "检查原密码")
    @ApiResponse(description = "检查原密码")
    @GetMapping(API_V1_PREFIX + "/user/check-ori-password")
    @RateLimiter(type = IP)
    public R<Boolean> checkOriPassword(@Parameter(description = "检查请求") @Valid UserOriPasswordCheckRequest request) {
        return R.success(userApi.checkOriPassword(request));
    }

    @Authenticated
    @OperationLog("修改用户头像")
    @Operation(summary = "修改用户头像")
    @ApiResponse(description = "修改用户头像")
    @PostMapping(value = API_V1_PREFIX + "/user/change-avatar")
    public R<Void> changeAvatar(@RequestBody @Valid UserChangeAvatarForm form) throws Exception {
        this.userApi.changeInfo(UserChangeInfoRequest.builder()
            .avatar(form.getAvatar())
            .build());
        return R.success();
    }

    @Authenticated
    @OperationLog("修改密码")
    @Operation(summary = "修改密码")
    @ApiResponse(description = "修改密码")
    @PostMapping(API_V1_PREFIX + "/user/change-password")
    public R<?> changePassword(@Parameter(description = "修改密码表单") @Valid UserChangePasswordForm form) {
        this.userApi.changePassword(form);
        return R.success();
    }

    @Authenticated
    @OperationLog("修改用户昵称")
    @Operation(summary = "修改用户昵称")
    @ApiResponse(description = "修改用户昵称")
    @PostMapping(value = API_V1_PREFIX + "/user/change-name")
    public R<Void> changeDisplayName(@RequestBody @Valid UserChangeDisplayNameForm form) throws Exception {
        userApi.changeInfo(UserChangeInfoRequest.builder()
            .displayName(form.getDisplayName())
            .build());
        return R.success();
    }

    @Authenticated
    @OperationLog("修改用户性别")
    @Operation(summary = "修改用户性别")
    @ApiResponse(description = "修改用户性别")
    @PostMapping(value = API_V1_PREFIX + "/user/change-sex")
    public R<Void> changeSex(@Parameter(description = "修改用户性别") @Valid UserChangeSexForm form) throws Exception {
        userApi.changeInfo(UserChangeInfoRequest.builder()
            .sex(form.getSex())
            .build());
        return R.success();
    }

    @Authenticated
    @OperationLog("修改用户出生日期")
    @Operation(summary = "修改用户出生日期")
    @ApiResponse(description = "修改用户出生日期")
    @PostMapping(value = API_V1_PREFIX + "/user/change-birthday")
    public R<Void> changeBirthday(@RequestBody @Valid UserChangeBirthdayForm form) throws Exception {
        userApi.changeInfo(UserChangeInfoRequest.builder()
            .birthday(form.getBirthday())
            .build());
        return R.success();
    }

}
