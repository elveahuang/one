package cc.wdev.platform.system.region.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "城市分组对象")
public class CityGroupVo implements Serializable {

    @Schema(description = "名称拼音首字母大写")
    private String titleFirstLetter;

    @Schema(description = "城市数组")
    private List<RegionVo> regions;
}
