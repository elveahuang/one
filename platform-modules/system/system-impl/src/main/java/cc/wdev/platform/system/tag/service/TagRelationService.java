package cc.wdev.platform.system.tag.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.tag.domain.entity.TagRelationEntity;
import cc.wdev.platform.system.tag.domain.request.TagDeleteRequest;
import cc.wdev.platform.system.tag.repository.TagRelationRepository;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * @author irving
 */
public interface TagRelationService extends
    CachingEntityService<TagRelationEntity, Long>, EnhancedEntityService<TagRelationEntity, Long, TagRelationRepository> {

    /**
     * 检查标签是否存在关联
     */
    Boolean hasRelation(Long tagId);

    /**
     * 删除标签关联
     */
    void deleteRelation(@NonNull Long tagId);

    /**
     * 删除标签关联
     */
    void deleteRelation(RelationRequest request);

    /**
     * 保存标签关联
     */
    void saveRelation(RelationSaveRequest request);

    /**
     * 查询标签关联
     */
    List<TagRelationEntity> findRelations(RelationRequest request);

    /**
     * 查询是否存在关联
     */
    Boolean hasRelation(TagDeleteRequest request);
}
