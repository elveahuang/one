package cc.wdev.platform.system.log.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author Irving
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "操作日志搜索请求")
public class OperationLogSearchRequest extends PageRequest {

    @Schema(title = "ID", defaultValue = "0", description = "用户ID")
    @Builder.Default
    private Long userId = 0L;

}
