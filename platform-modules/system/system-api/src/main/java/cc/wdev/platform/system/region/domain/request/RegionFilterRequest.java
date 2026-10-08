package cc.wdev.platform.system.region.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(title = "地区筛选请求参数", description = "地区筛选请求参数")
public class RegionFilterRequest extends Request {

    @Schema(title = "筛选关键词", description = "筛选关键词")
    @NotEmpty(message = "筛选关键词不能为空")
    private String q;
}
