package cc.wdev.platform.system.region.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

/**
 * 地区VO
 *
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "地区对象")
public class RegionVo implements Serializable {

    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "父级")
    private RegionVo parent;

    @Schema(description = "地区类型：COUNTRY-国家, PROVINCE-省份, CITY-城市, COUNTY-县区")
    private String type;

    @Schema(description = "地区编码")
    private String code;

    @Schema(description = "地区名称")
    private String title;

    @Schema(description = "是否有子节点")
    private Boolean hasChildren;

    @Schema(description = "字节点")
    private List<RegionVo> children;
}
