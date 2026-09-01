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

import java.util.List;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "保存实体消息状态")
public class ChatEntityMessageRequest extends Request {
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    /**
     * 消息ID列表
     */
    @Schema(description = "消息ID列表")
    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> chatMessageIdList;
    /**
     * 用户ID
     */
    @Schema(title = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
}
