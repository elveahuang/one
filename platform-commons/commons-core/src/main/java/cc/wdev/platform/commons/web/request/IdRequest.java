package cc.wdev.platform.commons.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdRequest {
    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;
}
