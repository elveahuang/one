package cc.wdev.platform.system.core.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)

@Schema(description = "租户套餐VO")
public class TenantPackageVo implements Serializable {

    @Schema(description = "租户套餐ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 文本
     */
    @Schema(description = "文本")
    private String label;
    /**
     * 套餐封面
     */
    @Schema(description = "套餐封面")
    private AttachmentVo cover;
    /**
     * 特权
     */
    @Schema(description = "特权")
    private String privilege;
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
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 来源
     */
    @Schema(description = "来源")
    private Integer source;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;
}
