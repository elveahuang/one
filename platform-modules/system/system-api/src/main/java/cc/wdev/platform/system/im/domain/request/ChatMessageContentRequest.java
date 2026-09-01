package cc.wdev.platform.system.im.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息内容查询对象")
public class ChatMessageContentRequest extends Request {
    /**
     * 会话ID
     */
    @Schema(title = "会话ID")
    private Long chatSessionId;
    /**
     * 消息ID
     */
    @Schema(title = "消息ID")
    private Long chatMessageId;
}
