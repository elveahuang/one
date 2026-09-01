package cc.wdev.platform.system.message.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息类型请求参数")
public class MessageTypePageRequest extends PageRequest {

    @Builder.Default
    @Schema(title = "是否包含消息模版", description = "是否包含消息模版")
    private boolean withItem = false;

}
