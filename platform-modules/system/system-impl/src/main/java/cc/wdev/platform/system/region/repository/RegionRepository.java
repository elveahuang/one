package cc.wdev.platform.system.region.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.core.domain.bo.EntityIndBo;
import cc.wdev.platform.system.region.domain.entity.RegionEntity;
import cc.wdev.platform.system.region.domain.request.RegionLocateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author erden
 */
@Mapper
public interface RegionRepository extends BaseEntityRepository<RegionEntity, Long> {

    /**
     * 批量获取是否有子节点
     */
    List<EntityIndBo> getHasChildrenBatch(@Param("parentIds") Collection<Long> parentIds);

    /**
     * 更新区域 GeoJSON 数据
     */
    void updateGeometry(@Param("code") String code, @Param("geometryJson") String geometryJson);

    /**
     * 经纬度定位
     */
    RegionEntity locate(@Param("request") RegionLocateRequest request);
}
