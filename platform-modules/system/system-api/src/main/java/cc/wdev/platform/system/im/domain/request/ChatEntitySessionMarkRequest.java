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

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息标记请求")
public class ChatEntitySessionMarkRequest extends Request {
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatMessageId;
    /**
     * 发送人ID
     */
    @Schema(title = "发送人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
}
