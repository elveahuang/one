package cc.wdev.platform.system.im.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "聊天室分页查询对象")
public class ChatSaveRequest extends Request {
    /**
     * ID
     */
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 目标用户ID
     */
    @Schema(description = "目标用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetUserId;
    /**
     * 会话用户ID
     */
    @Schema(description = "会话用户ID")
    private Long entitySessionUserId;
    /**
     * 最近一条消息的ID
     */
    @Schema(description = "最近一条消息的ID")
    private Long lastMessageId;
    /**
     * 更新人用户ID
     */
    @Schema(description = "更新人用户ID")
    private Long updatedBy;
}
