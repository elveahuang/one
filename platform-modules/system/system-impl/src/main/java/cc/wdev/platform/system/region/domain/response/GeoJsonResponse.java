package cc.wdev.platform.system.region.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 阿里云 GeoJSON 顶层响应类
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoJsonResponse {

    private String type; // 通常是 "FeatureCollection"
    private List<Feature> features; // 所有的行政区划数组

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        private String type; // 通常是 "Feature"
        private Properties properties; // 属性信息（包含 adcode、name 等）
        private Map<String, Object> geometry;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        private String adcode;       // 区域代码，如 440100
        private String name;         // 区域名称，如 "广州市"
        private String level;        // 级别，如 "city", "district"
        private Integer childrenNum; // 下级节点数

        private List<Double> center;
        private List<Double> centroid;
    }
}
