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
import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "岗位VO")
public class PositionVo implements Serializable {
    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "ID")
    private Long id;
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "租户ID")
    private Long tenantId;
    /**
     * 父级岗位ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "父级岗位ID")
    private Long parentId;
    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;
    /**
     * 文本
     */
    @Schema(description = "文本")
    private String label;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
    /**
     * 是否顶层岗位
     */
    @Schema(description = "是否顶层岗位")
    private Integer rootInd;
    /**
     * 是否默认岗位
     */
    @Schema(description = "是否默认岗位")
    private Integer defaultInd;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
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
    /**
     * 下级岗位
     */
    @Schema(description = "下级岗位")
    private List<PositionVo> children;

}
