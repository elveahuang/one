package cc.wdev.platform.system.tag.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.tag.domain.entity.TagRelationEntity;
import cc.wdev.platform.system.tag.domain.request.TagDeleteRequest;
import cc.wdev.platform.system.tag.repository.TagRelationRepository;
import cc.wdev.platform.system.tag.service.TagRelationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.TAG_RELATION;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class TagRelationServiceImpl
    extends BaseCachingEntityService<TagRelationEntity, Long, TagRelationRepository>
    implements TagRelationService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(TAG_RELATION);

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see TagRelationService#hasRelation(Long)
     */
    @Override
    public Boolean hasRelation(Long tagId) {
        return lambdaQueryWrapper().eq(TagRelationEntity::getTagId, tagId).exists();
    }

    /**
     * @see TagRelationService#deleteRelation(Long)
     */
    @Override
    public void deleteRelation(@NonNull Long tagId) {
        lambdaUpdateWrapper().eq(TagRelationEntity::getTagId, tagId).remove();
    }

    /**
     * @see TagRelationService#findRelations(RelationRequest)
     */
    public void deleteRelation(RelationRequest request) {
        List<TagRelationEntity> relationEntityList = this.findRelations(request);
        if (CollectionUtils.isNotEmpty(relationEntityList)) {
            this.deleteBatch(relationEntityList);
        }
    }

    /**
     * @see TagRelationService#saveRelation(RelationSaveRequest)
     */
    @Override
    public void saveRelation(RelationSaveRequest request) {
        String relationType = StringUtils.nvl(request.getRelationBizType(), request.getBizType());
        if (ObjectUtils.isValidId(request.getBizId()) && StringUtils.isNotEmpty(relationType)) {
            this.deleteRelation(RelationRequest.builder()
                .relationBizType(relationType)
                .bizType(request.getBizType())
                .bizId(request.getBizId())
                .build()
            );
        }

        List<TagRelationEntity> entityList = Arrays.stream(request.getIds()).map((id) -> TagRelationEntity.builder()
            .bizType(request.getRelationBizType())
            .bizId(request.getBizId())
            .tagId(id)
            .build()
        ).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(entityList)) {
            saveBatch(entityList);
            log.info("TagRelation Save success.");
        }
    }

    /**
     * @see TagRelationService#findRelations(RelationRequest)
     */
    @Override
    public List<TagRelationEntity> findRelations(RelationRequest request) {
        return lambdaQueryWrapper()
            .eq(TagRelationEntity::getBizType, request.getRelationBizType())
            .in(TagRelationEntity::getBizId, request.getBizIds())
            .list();
    }

    @Override
    public Boolean hasRelation(TagDeleteRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            return Boolean.FALSE;
        }
        return lambdaQueryWrapper()
            .in(TagRelationEntity::getTagId, request.getIds())
            .exists();
    }

}
