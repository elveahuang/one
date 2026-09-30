package cc.wdev.platform.commons.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "ID数组请求")
@EqualsAndHashCode(callSuper = true)
public class IdsRequest extends Request {

    @Schema(title = "ID数组", description = "ID数组")
    @NotEmpty(message = "ID不能为空")
    private List<Long> ids;
}
