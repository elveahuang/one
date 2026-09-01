package cc.wdev.platform.system.core.domain.dto;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "用户信息Dto")
public class UserInfoDto implements Serializable {
    /**
     * ID
     */
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    /**
     * 用户名
     */
    @Schema(description = "显示名")
    private String displayName;
    /**
     * 用户名
     */
    @Schema(description = "姓名")
    private String name;
    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private AttachmentVo avatar;
    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatarUrl;
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
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
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
     * 描述
     */
    @Schema(description = "描述")
    private String description;
    /**
     * 邀请码
     */
    @Schema(description = "邀请码")
    private String inviteCode;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
    /**
     * 最后修改时间
     */
    @Schema(description = "最后修改时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;
    /**
     * 用户所属角色
     */
    @Schema(description = "用户所属角色")
    private List<String> roles;
    /**
     * 用户角色ids
     */
    @Schema(description = "用户角色ids")
    private List<Long> roleIds;
    /**
     * 用户所拥有的权限
     */
    @Schema(description = "用户所拥有的权限")
    private List<String> authorities;
    /**
     * 用户所拥有的会员
     */
    @Schema(description = "用户所拥有的会员")
    private List<String> vips;
}
