package cc.wdev.platform.system.message.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息类型请求参数")
public class MessageChannelSearchRequest extends Request {

    @Schema(title = "消息类型", description = "消息类型")
    private String messageType;

    @Schema(title = "通道类型", description = "通道类型")
    private List<String> codeList;

    @Schema(title = "状态", description = "状态")
    private Integer status;

}
