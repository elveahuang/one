package cc.wdev.platform.system.commons.domain.chart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 饼图
 *
 * @author elvea
 */
@Data
@Builder
public class PieData {
    /**
     * 名称
     */
    @Schema(title = "名称", description = "名称")
    private String name;
    /**
     * 值
     */
    @Schema(title = "值", description = "值")
    private Long value;
}
