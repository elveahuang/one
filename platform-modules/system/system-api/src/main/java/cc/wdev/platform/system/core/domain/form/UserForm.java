package cc.wdev.platform.system.core.domain.form;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户表单")
public class UserForm implements Serializable {
    /**
     * ID
     */
    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
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
    @Schema(description = "电子邮箱")
    @Email(message = "电子邮箱格式不正确")
    private String email;
    /**
     * 全名
     */
    @Schema(description = "全名")
    @NotBlank(message = "全名不能为空")
    private String displayName;
    /**
     * 权限角色
     */
    @Schema(description = "权限角色")
    private List<Long> roleIds;
    /**
     * 性别
     */
    @Schema(description = "性别")
    private String sex;
    /**
     * 生日
     */
    @Schema(description = "生日")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_PATTERN)
    private LocalDate birthday;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
