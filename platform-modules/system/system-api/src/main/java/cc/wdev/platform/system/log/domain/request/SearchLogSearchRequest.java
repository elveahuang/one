package cc.wdev.platform.system.log.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author Irving
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchLogSearchRequest extends PageRequest {

    @Schema(title = "ID", defaultValue = "0", description = "用户ID")
    @Builder.Default
    private Long userId = 0L;

    @Schema(title = "开始时间", description = "开始时间")
    private LocalDateTime since;

}
