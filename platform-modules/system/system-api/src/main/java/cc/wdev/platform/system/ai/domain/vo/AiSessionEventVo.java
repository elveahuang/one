package cc.wdev.platform.system.ai.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.data.mybatis.domain.SimpleTenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * AI Session Event 实体
 *
 * @author elvea
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiSessionEventVo extends SimpleTenantEntity {

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /**
     * Session ID
     */
    @Schema(description = "Session ID")
    private String sessionId;

    /**
     * Timestamp（PostgreSQL 保留字，使用双引号转义）
     */
    @Schema(description = "Timestamp")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime timestamp;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String messageType;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String messageContent;

    /**
     * 消息数据
     */
    @Schema(description = "消息数据")
    private String messageData;

    /**
     * synthetic
     */
    @Schema(description = "synthetic")
    private Integer synthetic;

    /**
     * branch
     */
    @Schema(description = "branch")
    private String branch;

    /**
     * 压缩归档
     */
    @Schema(description = "archived")
    private Integer archived;

    /**
     * metadata
     */
    @Schema(description = "metadata")
    private String metadata;
}
