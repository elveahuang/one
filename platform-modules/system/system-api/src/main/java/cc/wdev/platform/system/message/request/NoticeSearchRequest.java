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
@Schema(description = "系统通知查询参数")
public class NoticeSearchRequest extends PageRequest {

    @Schema(title = "用户ID", defaultValue = "0", description = "用户ID")
    @Builder.Default
    private Long userId = 0L;

}
