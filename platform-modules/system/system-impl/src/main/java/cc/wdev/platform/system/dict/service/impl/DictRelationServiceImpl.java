package cc.wdev.platform.system.dict.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.dict.domain.entity.DictRelationEntity;
import cc.wdev.platform.system.dict.domain.request.DictDeleteRequest;
import cc.wdev.platform.system.dict.repository.DictRelationRepository;
import cc.wdev.platform.system.dict.service.DictRelationService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.DICT_RELATION;

/**
 * @author elvea
 */
@Slf4j
@Validated
@Service
@AllArgsConstructor
public class DictRelationServiceImpl extends BaseCachingEntityService<DictRelationEntity, Long, DictRelationRepository> implements DictRelationService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(DICT_RELATION);

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see DictRelationService#hasRelation(Long)
     */
    @Override
    public boolean hasRelation(@NonNull Long dictId) {
        return lambdaQueryWrapper().eq(DictRelationEntity::getDictId, dictId).exists();
    }

    /**
     * @see DictRelationService#deleteRelation(Long)
     */
    @Override
    public void deleteRelation(@NonNull Long dictId) {
        lambdaUpdateWrapper().eq(DictRelationEntity::getDictId, dictId).remove();
    }

    /**
     * @see DictRelationService#deleteRelation(Long)
     */
    @Override
    public void deleteRelation(@NonNull String code) {
        lambdaUpdateWrapper().eq(DictRelationEntity::getBizType, code).remove();
    }

    /**
     * @see DictRelationService#findRelations(RelationRequest)
     */
    public void deleteRelation(RelationRequest request) {
        List<DictRelationEntity> relationEntityList = this.findRelations(request);
        if (CollectionUtils.isNotEmpty(relationEntityList)) {
            this.deleteBatch(relationEntityList);
        }
    }

    /**
     * @see DictRelationService#saveRelation(RelationSaveRequest)
     */
    @Override
    public void saveRelation(RelationSaveRequest request) {
        String relationType = request.getRelationBizType();
        if (StringUtils.isNotEmpty(relationType)) {
            this.deleteRelation(RelationRequest.builder()
                .relationBizType(relationType)
                .bizId(request.getBizId())
                .build()
            );
        }

        if (ObjectUtils.isEmpty(request.getIds())) {
            request.setIds(new Long[]{});
        }

        List<DictRelationEntity> entityList = Arrays.stream(request.getIds()).map((id) -> DictRelationEntity.builder()
            .bizType(relationType)
            .bizId(request.getBizId())
            .dictId(id)
            .build()
        ).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(entityList)) {
            saveBatch(entityList);
            log.info("DictRelation save success.");
        }
    }

    /**
     * @see DictRelationService#findRelations(RelationRequest);
     */
    @Override
    public List<DictRelationEntity> findRelations(RelationRequest request) {
        List<Long> bizIds = Lists.newArrayList();
        if (ObjectUtils.isValidId(request.getBizId())) {
            bizIds.add(request.getBizId());
        }
        if (CollectionUtils.isNotEmpty(request.getBizIdList())) {
            bizIds.addAll(request.getBizIdList());
        }
        if (CollectionUtils.isEmpty(bizIds)) {
            return Collections.emptyList();
        }
        return lambdaQueryWrapper()
            .eq(DictRelationEntity::getBizType, request.getRelationBizType())
            .in(DictRelationEntity::getBizId, bizIds)
            .list();
    }

    @Override
    public Boolean hasRelation(DictDeleteRequest request) {
        return lambdaQueryWrapper()
            .in(DictRelationEntity::getDictId, request.getIds())
            .in(CollectionUtils.isNotEmpty(request.getBizIds()), DictRelationEntity::getBizId, request.getBizIds())
            .exists();
    }

}
