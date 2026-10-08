package cc.wdev.platform.system.region.api;

import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.region.domain.request.RegionFilterRequest;
import cc.wdev.platform.system.region.domain.request.RegionLocateRequest;
import cc.wdev.platform.system.region.domain.vo.CityGroupVo;
import cc.wdev.platform.system.region.domain.vo.RegionVo;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface RegionApi {

    // ------------------------------------------------------------------------------
    // Base
    // ------------------------------------------------------------------------------

    /**
     * 初始化系统智能体
     */
    void initialize();

    /**
     * 根据父级ID查询子地区列表
     *
     * @param parentId 父级ID，传0或null查询顶级地区（国家）
     * @return 地区列表
     */
    List<RegionVo> listByParentId(Long parentId);

    /**
     * 根据ID查询地区详情
     *
     * @param id 地区ID
     * @return 地区详情
     */
    RegionVo findById(Long id);

    /**
     * 根据ID列表批量查询地区
     *
     * @param ids 地区ID列表
     * @return 地区列表
     */
    List<RegionVo> findByIds(List<Long> ids);

    /**
     * 获取城市分组列表
     */
    List<CityGroupVo> cityGroups();

    /**
     * 保存地址关联关系
     */
    void saveRelation(RelationSaveRequest request);

    /**
     * 删除关联关系
     */
    void deleteRelation(RelationRequest request);

    /**
     * 获取地址关联关系
     */
    RelationVo<RegionVo> getRelation(RelationRequest request);

    /**
     * 获取关联关系
     */
    Map<Long, RelationVo<RegionVo>> relationMap(RelationRequest request);

    /**
     * 搜索地址
     */
    List<RegionVo> filterCities(RegionFilterRequest request);

    /**
     * 经纬度定位
     */
    RegionVo locate(RegionLocateRequest request);
}
