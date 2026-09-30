package cc.wdev.platform.system.im.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "聊天消息VO")
public class ChatMessageVo implements Serializable {
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long tenantId;
    /**
     * 消息ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 会话ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    /**
     * 发送人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderUserId;
    /**
     * 发送人类型
     */
    private String senderUserType;
    /**
     * 接收人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverUserId;
    /**
     * 发送人类型
     */
    private String receiverUserType;
    /**
     * 消息内容类型
     */
    private String messageContentType;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息附加内容
     */
    private String extra;
    /**
     * 版本号
     */
    private Long version;
    /**
     * 启用状态
     */
    private Integer active;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
    /**
     * 创建人
     */
    private Long createdBy;
    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;
    /**
     * 修改人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updatedBy;
    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
    /**
     * 删除人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deletedBy;
}
