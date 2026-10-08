package cc.wdev.platform.system.dict.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典标题校验请求")
public class DictTitleCheckRequest extends Request {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "租户ID")
    private Long tid;

    @Schema(description = "业务ID")
    private Long bid;

    @Schema(description = "业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    @Schema(description = "名称")
    private String title;
}
