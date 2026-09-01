package cc.wdev.platform.system.commons.domain.chart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 折线图
 *
 * @author elvea
 */
@Data
@Builder
public class LineData {
    /**
     * 名称
     */
    @Schema(title = "名称", description = "名称")
    private String name;
    /**
     * 类型
     */
    @Schema(title = "类型", description = "类型")
    private String type;
    /**
     * 数据
     */
    @Schema(title = "数据", description = "数据")
    private int[] data;
}
