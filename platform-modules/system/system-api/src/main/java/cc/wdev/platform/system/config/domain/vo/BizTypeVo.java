package cc.wdev.platform.system.config.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "业务类型VO")
public class BizTypeVo<T> implements Serializable {
    /**
     * 业务分组
     */
    @Schema(description = "业务分组")
    private String bizGroupType;
    /**
     * 业务范围
     */
    @Schema(description = "业务范围")
    private String bizScopeType;
    /**
     * 业务编号
     */
    @Schema(description = "业务编号")
    private String bizType;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 多语言文本
     */
    @Schema(description = "多语言文本")
    private String labelKey;
    /**
     * 多语言文本
     */
    @Schema(description = "多语言文本")
    private String labelGroup;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
    /**
     * 附加信息
     */
    @Schema(description = "附加信息")
    private String extra;
    /**
     * 业务配置
     */
    @Schema(description = "业务配置")
    private T config;
    /**
     * 序号
     */
    @Schema(description = "序号")
    private Integer idx;
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
