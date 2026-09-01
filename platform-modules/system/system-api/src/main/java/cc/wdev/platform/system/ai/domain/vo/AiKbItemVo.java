package cc.wdev.platform.system.ai.domain.vo;

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
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识条目")
public class AiKbItemVo implements Serializable {

    /**
     * ID
     */
    @Schema(title = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbId;

    /**
     * 业务类型
     */
    @Schema(title = "业务类型")
    private String bizType;

    /**
     * 业务ID
     */
    @Schema(title = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;

    /**
     * 类型
     */
    @Schema(title = "类型")
    private String type;

    /**
     * 标题
     */
    @Schema(title = "标题")
    private String title;

    /**
     * 问题（QA）
     */
    @Schema(title = "问题")
    private String question;

    /**
     * 答案（QA）
     */
    @Schema(title = "答案")
    private String answer;

    /**
     * 原始内容
     */
    @Schema(title = "原始内容")
    private String content;

    /**
     * 元数据
     */
    @Schema(title = "元数据", description = "元数据")
    private String metadata;

    /**
     * 内容类型
     */
    @Schema(title = "内容类型")
    private String contentType;

    /**
     * 内容哈希
     */
    @Schema(title = "内容哈希")
    private String contentHash;

    /**
     * 内容大小
     */
    @Schema(title = "内容大小")
    private Long contentSize;

    /**
     * 分片策略
     */
    @Schema(title = "分片策略")
    private String chunkStrategy;

    /**
     * 是否已向量化
     */
    @Schema(title = "是否已向量化")
    private Integer vectorized;

    /**
     * 状态
     */
    @Schema(title = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(title = "更新时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

}
