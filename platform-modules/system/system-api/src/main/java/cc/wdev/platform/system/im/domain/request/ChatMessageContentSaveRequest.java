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
@Schema(description = "消息内容查询对象")
public class ChatMessageContentSaveRequest extends Request {
    /**
     * 会话ID
     */
    @Schema(title = "会话ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    /**
     * 消息ID
     */
    @Schema(title = "消息ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatMessageId;
    /**
     * 内容ID
     */
    @Schema(title = "内容ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 内容详情
     */
    private String type;
    /**
     * 内容详情
     */
    private String content;
    /**
     * 附加信息
     */
    private String extra;
}
