package cc.wdev.platform.system.region.domain.request;

import cc.wdev.platform.commons.enums.RegionTypeEnum;
import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(title = "地区定位请求参数", description = "地区定位请求参数")
public class RegionLocateRequest extends Request {

    @Builder.Default
    @Schema(description = "类型")
    private List<String> types = List.of(RegionTypeEnum.CITY.getValue());

    @NotNull(message = "经度不能为空")
    @Schema(description = "经度")
    private Double longitude;

    @NotNull(message = "纬度不能为空")
    @Schema(description = "纬度")
    private Double latitude;
}
