package cc.wdev.platform.commons.security.domain;

import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.SocialTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUser implements Serializable {
    /**
     * socialType
     */
    private String socialType;
    /**
     * 邀请码
     */
    private String inviteCode;
    /**
     * 邀请人用户ID
     */
    private Long inviteBy;
    /**
     * openId
     */
    private String openId;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String headImgUrl;
    /**
     * 性别
     * 1为男性，2为女性
     */
    private Integer sex;
    /**
     * unionId
     */
    private String unionId;
    /**
     * rawInfo
     */
    private Map<String, Object> rawInfo;

    public String getPrincipal() {
        SocialTypeEnum otpTypeEnum = BaseEnum.getEnumByValue(this.getSocialType(), SocialTypeEnum.class);
        if (SocialTypeEnum.WECHAT_MP.equals(otpTypeEnum)) {
            return this.unionId;
        } else if (SocialTypeEnum.WECHAT_MA.equals(otpTypeEnum)) {
            return this.unionId;
        }
        return "NONE_PROVIDED";
    }

}
