package cc.wdev.platform.commons.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ID数组请求")
@EqualsAndHashCode(callSuper = true)
public class BizRequest extends Request {

    @Schema(description = "业务ID")
    @NotNull(message = "业务ID不能为空")
    private Long bizId;

    @Schema(description = "业务类型")
    @NotNull(message = "业务类型不能为空")
    @NotEmpty
    private String bizType;
}
