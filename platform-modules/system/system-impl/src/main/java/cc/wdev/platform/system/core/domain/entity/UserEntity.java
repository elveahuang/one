package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户实体")
public class UserEntity extends BaseTenantEntity {
    /**
     * 用户标识
     */
    @Schema(description = "用户ID")
    private String uuid;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String displayName;
    /**
     * 全名
     */
    @Schema(description = "全名")
    private String name;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
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
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    /**
     * 证件类型
     */
    @Schema(description = "证件类型")
    private String idCardType;
    /**
     * 证件号码
     */
    @Schema(description = "证件号码")
    private String idCardNo;
    /**
     * 性别
     */
    @Schema(description = "性别")
    private String sex;
    /**
     * 生日
     */
    @Schema(description = "生日")
    private LocalDate birthday;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 个性签名
     */
    @Schema(description = "个性签名")
    private String signature;
    /**
     * 邀请码
     */
    @Schema(description = "邀请码")
    private String inviteCode;
    /**
     * 邀请人
     */
    @Schema(description = "邀请人")
    private long inviteBy;
    /**
     * 用户状态
     */
    @Schema(description = "用户状态")
    private Integer status;
    /**
     * 密码过期时间
     */
    @Schema(description = "密码过期时间")
    private String passwordExpireAt;
    /**
     * 最后一次输入错误密码的时间
     */
    @Schema(description = "最后一次输入错误密码的时间")
    private String passwordErrorAt;
    /**
     * 输入错误密码的次数
     */
    @Schema(description = "输入错误密码的次数")
    private Integer passwordErrorCount;
    /**
     * Telegram
     */
    @Schema(description = "Telegram")
    private String telegram;
    /**
     * 最后登录状态
     */
    @Schema(description = "最后登录状态")
    private String lastLoginStatus;
    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private String lastLoginAt;
}
