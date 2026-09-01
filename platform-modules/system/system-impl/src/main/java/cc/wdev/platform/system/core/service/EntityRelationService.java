package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.core.domain.dto.EntityRelationSaveDto;
import cc.wdev.platform.system.core.domain.entity.EntityRelationEntity;

import java.util.List;

/**
 * @author elvea
 */
public interface EntityRelationService extends CachingEntityService<EntityRelationEntity, Long> {

    /**
     * 保存实体关联
     */
    void saveEntityRelation(EntityRelationSaveDto saveDto);

    /**
     * 获取所有上级
     *
     * @param relationType 关联类型
     * @param entityId     实体ID
     * @return List
     */
    List<EntityRelationEntity> getParents(String relationType, Long entityId);

    /**
     * 获取所有直属上级
     *
     * @param relationType 关联类型
     * @param entityId     实体ID
     * @return List
     */
    List<EntityRelationEntity> getDirectParents(String relationType, Long entityId);

    /**
     * 获取所有下级
     *
     * @param relationType 关联类型
     * @param ancestorId   实体ID
     * @return List
     */
    List<EntityRelationEntity> getChildren(String relationType, Long ancestorId);

    /**
     * 获取所有直属下级
     *
     * @param relationType 关联类型
     * @param ancestorId   实体ID
     * @return List
     */
    List<EntityRelationEntity> getDirectChildren(String relationType, Long ancestorId);

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
