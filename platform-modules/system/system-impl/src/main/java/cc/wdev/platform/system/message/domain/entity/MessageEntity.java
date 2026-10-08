package cc.wdev.platform.system.message.domain.entity;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.data.core.domain.TenantEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
@Schema(description = "消息实体")
public class MessageEntity extends BaseEntity implements TenantEntity {
    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String subject;
    /**
     * 标题
     */
    @Schema(description = "链接")
    private String url;
    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;
    /**
     * 数据
     */
    @Schema(description = "数据")
    private String data;
    /**
     * 发布状态
     */
    @Schema(description = "发布状态")
    private Integer status;
    /**
     * 目标发送时间
     */
    @Schema(description = "目标发送时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN)
    private LocalDateTime targetSentDatetime;
    /**
     * 发送时间
     */
    @Schema(description = "发送时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN)
    private LocalDateTime sentDatetime;
    /**
     * 尝试发送次数
     */
    @Schema(description = "尝试发送次数")
    private Integer attempt;
}
