package cc.wdev.platform.system.ai.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "智能体VO")
public class AiChatVo implements Serializable {

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
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 对话类型
     */
    @Schema(description = "对话类型")
    private String type;

    /**
     * 对话标识
     */
    @Schema(description = "对话标识")
    private String conversationId;

    /**
     * 对话标题
     */
    @Schema(description = "对话标题")
    private String title;

    /**
     * 消息列表
     */
    @Schema(description = "消息列表")
    private List<Message> messages;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

}
