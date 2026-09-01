package cc.wdev.platform.system.region.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.region.domain.entity.RegionRelationEntity;

import java.util.List;

public interface RegionRelationService extends EntityService<RegionRelationEntity, Long> {
    /**
     * 获取实体与地址关联
     */
    List<RegionRelationEntity> findRelations(RelationRequest request);

    /**
     * 保存实体与地址关联
     */
    void saveRelation(RelationSaveRequest request);

    /**
     * 删除实体与地址关联
     */
    void deleteRelation(RelationRequest request);
}
