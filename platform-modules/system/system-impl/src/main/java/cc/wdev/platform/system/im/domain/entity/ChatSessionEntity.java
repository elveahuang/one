package cc.wdev.platform.system.im.domain.entity;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_chat_session")
public class ChatSessionEntity extends BaseTenantEntity {
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 业务实体ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 目标用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetUserId;
    /**
     * 最新消息的ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastMessageId;
    /**
     * 最后消息发送时间
     */
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime lastMessageSendAt;
    /**
     *
     */
    private Integer status;
}
