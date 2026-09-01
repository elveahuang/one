package cc.wdev.platform.system.core.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
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
@Schema(description = "角色VO")
public class RoleVo implements Serializable {
    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "ID")
    protected Long id;
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "租户ID")
    protected Long tenantId;
    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;
    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;
    /**
     * 分组类型
     */
    @Schema(description = "分组类型")
    private String groupType;
    /**
     * 文本
     */
    @Schema(description = "文本")
    private String label;
    /**
     * 名称
     */
    @Schema(description = "名称")
    private String title;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 数据范围
     */
    @Schema(title = "数据范围", description = "数据范围")
    private String dataScopeType;
    /**
     * 是否为默认角色
     */
    @Schema(title = "是否为默认角色", description = "是否为默认角色")
    private Integer defaultInd;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
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
