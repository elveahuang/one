package cc.wdev.platform.system.im.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "聊天室批量查询对象")
public class ChatSearchRequest extends PageRequest {

    @Schema(description = "业务类型")
    @NotEmpty(message = "业务类型不能为空")
    private String bizType;

    @Schema(description = "业务ID")
    @NotEmpty(message = "业务ID不能为空")
    private Collection<Long> bizIds;

    @Schema(description = "用户ID")
    private Collection<Long> userIds;
}
