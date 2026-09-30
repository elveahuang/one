package cc.wdev.platform.system.tag.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author irving
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "标签VO")
public class TagVo implements Serializable {
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 标题
     */
    @Schema(title = "标题", description = "标题")
    private String title;
    /**
     * 序号
     */
    @Schema(title = "序号", description = "序号")
    private Integer idx;
    /**
     * 描述
     */
    @Schema(title = "描述", description = "描述")
    private String description;
    /**
     * 来源
     */
    @Schema(title = "来源", description = "来源")
    private Integer source;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;
}
