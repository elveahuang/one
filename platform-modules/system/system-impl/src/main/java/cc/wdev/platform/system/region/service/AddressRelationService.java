package cc.wdev.platform.system.region.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.region.domain.entity.AddressRelationEntity;
import cc.wdev.platform.system.region.domain.request.AddressDeleteRequest;

import java.util.List;

/**
 * @author erden
 * @see EntityService
 */
public interface AddressRelationService extends CachingEntityService<AddressRelationEntity, Long> {
    /**
     * 获取实体与地址关联
     */
    List<AddressRelationEntity> findRelations(RelationRequest request);

    /**
     * 根据地址ID查询关联的业务ID列表
     */
    List<Long> findBizIdsByAddressId(Long addressId, String relationBizType);

    /**
     * 保存实体与地址关联
     */
    void saveRelation(RelationSaveRequest request);

    /**
     * 删除实体与地址关联
     */
    void deleteRelation(RelationRequest request);

    /**
     * 查询是否存在关联关系
     */
    Boolean hasRelated(AddressDeleteRequest request);
}
