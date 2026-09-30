package cc.wdev.platform.system.catalog.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.catalog.domain.entity.CatalogRelationEntity;
import cc.wdev.platform.system.catalog.domain.request.CatalogRelationSaveRequest;

import java.util.List;

/**
 * @author elvea
 */
public interface CatalogRelationService extends CachingEntityService<CatalogRelationEntity, Long> {

    /**
     * 保存分类关联
     */
    void saveCatalogRelation(CatalogRelationSaveRequest saveDto);

    /**
     * 获取所有关联
     */
    List<CatalogRelationEntity> getRelation(String relationType, Long entityId);

    /**
     * 获取所有上级
     *
     * @param relationType 关联类型
     * @param entityId     实体ID
     * @return List
     */
    List<CatalogRelationEntity> getParents(String relationType, Long entityId);

    /**
     * 获取所有直属上级
     *
     * @param relationType 关联类型
     * @param entityId     实体ID
     * @return List
     */
    List<CatalogRelationEntity> getDirectParents(String relationType, Long entityId);

    /**
     * 获取所有下级
     *
     * @param relationType 关联类型
     * @param ancestorId   实体ID
     * @return List
     */
    List<CatalogRelationEntity> getChildren(String relationType, Long ancestorId);

    /**
     * 获取所有直属下级
     *
     * @param relationType 关联类型
     * @param ancestorId   实体ID
     * @return List
     */
    List<CatalogRelationEntity> getDirectChildren(String relationType, Long ancestorId);

    /**
     * 删除下级关联
     *
     * @param relationType 关联类型
     * @param ancestorId   实体ID
     */
    void deleteAsAncestor(String relationType, Long ancestorId);

    /**
     * 删除下级关联
     *
     * @param relationType   关联类型
     * @param ancestorIdList 实体ID集合
     */
    void deleteAsAncestor(String relationType, List<Long> ancestorIdList);

    /**
     * 删除上级关联
     *
     * @param relationType 关联类型
     * @param entityId     实体ID
     */
    void deleteAsChild(String relationType, Long entityId);

    /**
     * 删除上级关联
     *
     * @param relationType 关联类型
     * @param entityIdList 实体ID集合
     */
    void deleteAsChild(String relationType, List<Long> entityIdList);

}
