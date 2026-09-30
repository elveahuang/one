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
@Schema(description = "添加套餐请求")
public class AddTenantPackageForm {
    /**
     * 编号
     */
    @Schema(description = "编号")
    @Size(max = 150, message = "编号文本长度不能超过150个字符")
    private String code;
    /**
     * 套餐名称
     */
    @Schema(description = "套餐名称")
    @NotBlank(message = "套餐名称不能为空")
    @Size(max = 150, message = "套餐名称不能超过150个字符")
    private String title;
    /**
     * 内容
     */
    @Schema(description = "内容")
    @Size(max = 150, message = "内容不能超过150个字符")
    private String label;
    /**
     * 特权
     */
    @Schema(description = "特权")
    @Size(max = 1000, message = "特权文本不能超过1000个字符")
    private String privilege;
    /**
     * 封面
     */
    @Schema(description = "封面")
//    @NotNull(message = "封面不能为空")
    private AttachmentVo cover;
    /**
     * 是否默认
     */
    @Schema(description = "是否默认")
    private Integer defaultInd;
    /**
     * 是否允许试用
     */
    @Schema(description = "是否允许试用")
    private Integer trialInd;
    /**
     * 试用时长，单位是自然天
     */
    @Schema(description = "试用时长，单位是自然天")
    private Integer trialLimit;
    /**
     * 会员等级，等级越高显示优先级越高
     */
    @Schema(description = "会员等级，等级越高显示优先级越高")
    private Integer level;
    /**
     * 序号
     */
    @Schema(description = "序号")
    private Integer idx;
    /**
     * 描述
     */
    @Schema(description = "描述")
    @Size(max = 255, message = "描述说明文本长度不能超过255个字符")
    private String description;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
