package cc.wdev.platform.system.commons.controller;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.annotations.RateLimiter;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.CaptchaTypeEnum;
import cc.wdev.platform.commons.extensions.captcha.Captcha;
import cc.wdev.platform.commons.extensions.captcha.domain.CaptchaCodeDto;
import cc.wdev.platform.commons.extensions.captcha.domain.CaptchaDto;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.api.CaptchaApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;
import static cc.wdev.platform.commons.enums.RateLimitTypeEnum.IP;
import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "CaptchaController", description = "验证码控制器")
public class CaptchaController extends AbstractController {

    private final CaptchaApi captchaApi;

    @Anonymous
    @Operation(summary = "获取验证码")
    @ApiResponse(description = "获取验证码")
    @PostMapping(API_V1_PREFIX + "/captcha/code")
    @RateLimiter(type = IP, limit = 1)
    public R<CaptchaCodeDto> captchaCode() throws Exception {
        CaptchaRequest request = CaptchaRequest.builder().type(CaptchaTypeEnum.CODE).size(4).build();
        Captcha captcha = this.captchaApi.generate(request);
        return R.success(CaptchaCodeDto.builder().key(captcha.getKey()).image(captcha.getImage()).build());
    }

    @Anonymous
    @OperationLog("校验验证码")
    @Operation(summary = "校验验证码")
    @ApiResponse(description = "校验验证码")
    @PostMapping(value = {API_V1_PREFIX + "/captcha/code/check", EXCHANGE_PREFIX + "/captcha/generate"})
    public R<Boolean> captchaCodeCheck(@Parameter(description = "验证码key") @RequestParam("captchaKey") String captchaKey,
                                       @Parameter(description = "验证码") @RequestParam("captchaValue") String captchaValue) {
        CaptchaCheckRequest request = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.CODE)
            .key(captchaKey)
            .value(captchaValue)
            .build();
        return R.success(this.captchaApi.check(request));
    }

    @Anonymous
    @Operation(summary = "获取邮件验证码")
    @ApiResponse(description = "获取邮件验证码")
    @PostMapping(API_V1_PREFIX + "/captcha/mail")
    @RateLimiter(type = IP, limit = 1)
    public R<CaptchaDto> captchaEmail(@Parameter(description = "邮箱") @RequestParam("email") String email) throws Exception {
        CaptchaRequest request = CaptchaRequest.builder().type(CaptchaTypeEnum.EMAIL).size(6).email(email).build();
        Captcha captcha = this.captchaApi.generate(request);
        return R.success(CaptchaDto.builder().key(captcha.getKey()).build());
    }

    @Anonymous
    @OperationLog("校验邮件验证码")
    @Operation(summary = "校验邮件验证码")
    @ApiResponse(description = "校验邮件验证码")
    @PostMapping(API_V1_PREFIX + "/captcha/mail/check")
    public R<Boolean> captchaEmailCheck(@Parameter(description = "邮箱") @RequestParam("email") String email,
                                        @Parameter(description = "验证码key") @RequestParam("captchaKey") String captchaKey,
                                        @Parameter(description = "验证码") @RequestParam("captchaValue") String captchaValue) {
        CaptchaCheckRequest request = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.EMAIL)
            .email(email)
            .key(captchaKey)
            .value(captchaValue)
            .build();
        return R.success(this.captchaApi.check(request));
    }

    @Anonymous
    @Operation(summary = "获取手机验证码")
    @ApiResponse(description = "获取手机验证码")
    @PostMapping(API_V1_PREFIX + "/captcha/sms")
    @RateLimiter(type = IP, limit = 1)
    public R<CaptchaDto> captchaMobile(@Parameter(description = "国家区号") @RequestParam("mobileCountryCode") String mobileCountryCode,
                                       @Parameter(description = "手机号") @RequestParam("mobileNumber") String mobileNumber) throws Exception {
        CaptchaRequest request = CaptchaRequest.builder()
            .type(CaptchaTypeEnum.SMS)
            .mobileCountryCode(mobileCountryCode)
            .mobileNumber(mobileNumber)
            .size(6)
            .build();
        Captcha captcha = this.captchaApi.generate(request);
        return R.success(CaptchaDto.builder().key(captcha.getKey()).build());
    }

    @Anonymous
    @OperationLog("校验手机验证码")
    @Operation(summary = "校验手机验证码")
    @ApiResponse(description = "校验手机验证码")
    @PostMapping(API_V1_PREFIX + "/captcha/sms/check")
    public R<Boolean> captchaMobileCheck(@Parameter(description = "国家区号") @RequestParam("mobileCountryCode") String mobileCountryCode,
                                         @Parameter(description = "手机号") @RequestParam("mobileNumber") String mobileNumber,
                                         @Parameter(description = "验证码key") @RequestParam("captchaKey") String captchaKey,
                                         @Parameter(description = "验证码值") @RequestParam("captchaValue") String captchaValue) {
        CaptchaCheckRequest request = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.SMS)
            .mobileNumber(mobileNumber)
            .mobileCountryCode(mobileCountryCode)
            .key(captchaKey)
            .value(captchaValue)
            .build();
        return R.success(this.captchaApi.check(request));
    }

}
