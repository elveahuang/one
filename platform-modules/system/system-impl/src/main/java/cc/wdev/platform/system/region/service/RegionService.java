package cc.wdev.platform.system.region.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.system.region.domain.entity.RegionEntity;
import cc.wdev.platform.system.region.domain.request.RegionFilterRequest;
import cc.wdev.platform.system.region.domain.request.RegionLocateRequest;
import cc.wdev.platform.system.region.domain.vo.RegionVo;
import cc.wdev.platform.system.region.repository.RegionRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface RegionService extends EnhancedEntityService<RegionEntity, Long, RegionRepository> {

    /**
     * 定时从民政部同步行政区划数据
     */
    void syncMcaData() throws Exception;

    /**
     * 根据父级ID查询子地区列表（包含 hasChildren 标识）
     * 用于前端级联选择器懒加载
     */
    List<RegionEntity> findByParentId(Long parentId);

    /**
     * 查询全部市级和区级地区
     */
    List<RegionEntity> findCitiesAll();

    /**
     * 搜索地区城市
     */
    List<RegionEntity> filterCities(RegionFilterRequest request);

    /**
     * 批量获取是否有子节点
     */
    Map<Long, Boolean> getHasChildrenBatch(Collection<Long> parentIds);

    /**
     * 获取子节点
     */
    void findChildrenByParentId(RegionVo regionVo);

    /**
     * 获取地区边缘数据GeoJSON
     */
    void getRegionGeoJson() throws Exception;

    /**
     * 经纬度定位
     */
    RegionEntity locate(RegionLocateRequest request);
}
