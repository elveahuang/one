package cc.wdev.platform.system.core.domain.form;

import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "新增租户表单")
public class AddTenantForm {

    /**
     * 编号
     */
    @Schema(description = "编号")
    @Size(max = 20, message = "编号长度不能超过20个字符")
    private String code;

    /**
     * 标题
     */
    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    /**
     * 简介
     */
    @Schema(description = "简介")
    private String details;
    /**
     * 租户地址
     */
    @Schema(description = "租户地址")
    @NotBlank(message = "租户地址不能为空")
    @Size(max = 255, message = "租户地址长度不能超过255个字符")
    private String address;
    /**
     * 租户域名
     */
    @Schema(description = "租户域名")
    @NotBlank(message = "租户域名不能为空")
    @Size(max = 255, message = "租户域名长度不能超过255个字符")
    private String domain;
    /**
     * 联系人
     */
    @Schema(description = "联系人")
    @NotBlank(message = "联系人不能为空")
    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactUser;
    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    @Size(max = 50, message = "联系电话长度不能超过50个字符")
    private String contactPhone;
    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    @NotBlank(message = "租户名称不能为空")
    @Size(max = 50, message = "租户名称长度不能超过50个字符")
    private String companyName;
    /**
     * 租户执照号
     */
    @Schema(description = "租户执照号")
    @NotBlank(message = "租户执照号不能为空")
    @Size(max = 50, message = "租户执照号长度不能超过50个字符")
    private String companyLicenseNumber;
    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    @Size(max = 255, message = "备注说明长度不能超过255个字符")
    private String description;
    /**
     * 租户封面
     */
//    @NotNull(message = "租户封面不能为空")
    @Schema(description = "租户封面")
    private AttachmentVo cover;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
    /**
     * 租户用户数
     */
    @Schema(description = "租户用户数")
    private Integer accountCount;
}
