package cc.wdev.platform.system.dict.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.dict.domain.entity.DictRelationEntity;
import cc.wdev.platform.system.dict.domain.request.DictDeleteRequest;
import cc.wdev.platform.system.dict.repository.DictRelationRepository;
import jakarta.validation.Valid;

import java.util.List;

/**
 * @author elvea
 */
public interface DictRelationService extends CachingEntityService<DictRelationEntity, Long>, EnhancedEntityService<DictRelationEntity, Long, DictRelationRepository> {

    /**
     * 判断字典是否含有关联
     */
    boolean hasRelation(Long dictId);

    /**
     * 通过字典ID和字典项ID删除关联关系
     */
    void deleteRelation(Long dictId);

    /**
     * 通过字典编码删除关联关系
     */
    void deleteRelation(String dictCode);

    /**
     * 通过目标对象删除关联
     */
    void deleteRelation(RelationRequest request);

    /**
     * 保存字典关联
     */
    void saveRelation(RelationSaveRequest request);

    /**
     * 获取实体与字典关联
     */
    List<DictRelationEntity> findRelations(RelationRequest request);

    /**
     * 查询是否存在关联
     */
    Boolean hasRelation(@Valid DictDeleteRequest request);
}
