package cc.wdev.platform.commons.security.domain;

import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.OtpTypeEnum;
import cc.wdev.platform.commons.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpUser implements Serializable {
    /**
     * otpType
     */
    private String otpType;
    /**
     * 邀请码
     */
    private String inviteCode;
    /**
     * 邀请人用户ID
     */
    private Long inviteBy;
    /**
     * 手机国家区号
     */
    @Schema(description = "手机国家区号")
    private String mobileCountryCode;
    /**
     * 手机
     */
    @Schema(description = "手机")
    private String mobileNumber;
    /**
     * 电子邮箱
     */
    private String email;

    public String getPrincipal() {
        OtpTypeEnum otpTypeEnum = BaseEnum.getEnumByValue(this.getOtpType(), OtpTypeEnum.class);
        if (OtpTypeEnum.EMAIL.equals(otpTypeEnum)) {
            return this.email;
        } else if (OtpTypeEnum.SMS.equals(otpTypeEnum)) {
            return "%s:%s".formatted(StringUtils.nvl(mobileCountryCode, "86"), this.mobileNumber);
        }
        return "NONE_PROVIDED";
    }

}
