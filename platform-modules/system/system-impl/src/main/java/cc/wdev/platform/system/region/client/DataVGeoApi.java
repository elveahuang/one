package cc.wdev.platform.system.region.client;

import cc.wdev.platform.system.region.domain.response.GeoJsonResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 阿里云 DataV 地理边界 GeoJSON HTTP 客户端
 *
 * @author erden
 */
@HttpExchange
public interface DataVGeoApi {

    String BASE_URL = "https://geo.datav.aliyun.com/areas_v3/bound";

    /**
     * 获取行政区划 GeoJSON 边界数据
     *
     * @param path 文件路径（如 "100000.json"、"100000_full.json"、"440000_full_district.json"）
     * @return 解析后的 GeoJsonResponse 对象
     */
    @GetExchange("/{path}")
    GeoJsonResponse getGeoJson(@PathVariable("path") String path);

}
