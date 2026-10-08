package cc.wdev.platform.system.commons.domain.chart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 基础图表数据
 *
 * @author elvea
 */
@Data
@Builder
@Schema(description = "基础图表数据")
public class ChartVo {
    /**
     * 通用属性
     */
    @Schema(description = "图例列表")
    private List<String> legendList;
    @Schema(description = "X 轴数据列表")
    private List<String> xAxisDataList;
    /**
     * 饼图数据
     */
    @Schema(description = "饼图数据列表")
    private List<PieData> pieDataList;
    /**
     * 折线图数据
     */
    @Schema(description = "折线图数据列表")
    private List<LineData> lineDataList;
    /**
     * 柱状图数据
     */
    @Schema(description = "柱状图数据列表")
    private List<List<Long>> barDataList;
}
