package cc.wdev.platform.system.message.domain.entity;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
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
@TableName("sys_notice")
@Schema(description = "系统通知实体")
public class NoticeEntity extends BaseTenantEntity {
    /**
     * 通知标题
     */
    @Schema(description = "通知标题")
    private String subject;
    /**
     * 通知内容
     */
    @Schema(description = "通知内容")
    private String content;
    /**
     * 收件人ID
     */
    @Schema(description = "收件人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long recipientId;
    /**
     * 发件人ID
     */
    @Schema(description = "发件人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;
    /**
     * 是否已读
     */
    @Schema(description = " 是否已读")
    private Boolean readInd;
    /**
     * 发送时间
     */
    @Schema(description = "发送时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_FULL_DATE_TIME_PATTERN)
    private LocalDateTime readDatetime;
}
