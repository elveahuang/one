package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.core.domain.dto.EntityRelationSaveDto;
import cc.wdev.platform.system.core.domain.entity.EntityRelationEntity;
import cc.wdev.platform.system.core.repository.EntityRelationRepository;
import cc.wdev.platform.system.core.service.EntityRelationService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author elvea
 * @see EntityRelationService
 * @see BaseCachingEntityService
 */
@Slf4j
@Service
public class EntityRelationServiceImpl extends BaseCachingEntityService<EntityRelationEntity, Long, EntityRelationRepository>
    implements EntityRelationService {

    private final CacheKeyGenerator cacheKeyGenerator = SimpleCacheKeyGenerator.builder().prefix(SystemCacheConstants.ENTITY_RELATION).build();

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see EntityRelationService#saveEntityRelation(EntityRelationSaveDto)
     */
    @Override
    public void saveEntityRelation(EntityRelationSaveDto saveDto) {
        String ancestorRelationType = StringUtils.isNotEmpty(saveDto.getAncestorRelationType()) ?
            saveDto.getAncestorRelationType() : saveDto.getRelationType();

        List<EntityRelationEntity> relationList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(saveDto.getAncestorIdList())) {
            for (Long ancestorId : saveDto.getAncestorIdList()) {
                List<EntityRelationEntity> entityRelationList = com.google.common.collect.Lists.newArrayList();

                AtomicInteger index = new AtomicInteger(1);

                List<EntityRelationEntity> ancestorRelationList = this.getParents(ancestorRelationType, ancestorId);

                StringBuilder sb = new StringBuilder();

                // 增加直接上级的上级关联
                if (CollectionUtils.isNotEmpty(ancestorRelationList)) {
                    entityRelationList.addAll(ancestorRelationList.stream().map(r -> {
                        // 关联路径
                        sb.append(GlobalConstants.DELIMITER).append(r.getAncestorId());
                        // 构建关联
                        return EntityRelationEntity.builder()
                            .ancestorId(r.getAncestorId())
                            .entityId(saveDto.getEntityId())
                            .parentInd(BooleanTypeEnum.FALSE.getValue())
                            .relationType(saveDto.getRelationType())
                            .relationIndex(index.getAndIncrement())
                            .build();
                    }).toList());
                }

                // 增加直接上级的关联
                entityRelationList.add(EntityRelationEntity.builder()
                    .ancestorId(ancestorId)
                    .entityId(saveDto.getEntityId())
                    .parentInd(BooleanTypeEnum.TRUE.getValue())
                    .relationType(saveDto.getRelationType())
                    .relationIndex(index.getAndIncrement())
                    .build()
                );

                sb.append(GlobalConstants.DELIMITER).append(ancestorId).append(GlobalConstants.DELIMITER);

                // 处理完整关联路径
                entityRelationList.forEach(r -> r.setRelationPath(sb.toString()));

                relationList.addAll(entityRelationList);
            }
        }
        // 删除已有的关联记录
        this.deleteAsChild(saveDto.getRelationType(), saveDto.getEntityId());
        // 保存新的关联记录
        if (CollectionUtils.isNotEmpty(relationList)) {
            this.saveBatch(relationList);
        }
    }

    /**
     * @see EntityRelationService#getParents(String, Long)
     */
    @Override
    public List<EntityRelationEntity> getParents(String relationType, Long entityId) {
        return lambdaQueryWrapper()
            .eq(EntityRelationEntity::getRelationType, relationType)
            .eq(EntityRelationEntity::getEntityId, entityId)
            .list();
    }

    /**
     * @see EntityRelationService#getDirectParents(String, Long)
     */
    @Override
    public List<EntityRelationEntity> getDirectParents(String relationType, Long entityId) {
        return lambdaQueryWrapper()
            .eq(EntityRelationEntity::getRelationType, relationType)
            .eq(EntityRelationEntity::getEntityId, entityId)
            .eq(EntityRelationEntity::getParentInd, BooleanTypeEnum.TRUE.getValue())
            .list();
    }

    /**
     * @see EntityRelationService#getChildren(String, Long)
     */
    @Override
    public List<EntityRelationEntity> getChildren(String relationType, Long ancestorId) {
        return lambdaQueryWrapper()
            .eq(EntityRelationEntity::getRelationType, relationType)
            .eq(EntityRelationEntity::getAncestorId, ancestorId)
            .list();
    }

    /**
     * @see EntityRelationService#getDirectChildren(String, Long)
     */
    @Override
    public List<EntityRelationEntity> getDirectChildren(String relationType, Long ancestorId) {
        return lambdaQueryWrapper()
            .eq(EntityRelationEntity::getRelationType, relationType)
            .eq(EntityRelationEntity::getAncestorId, ancestorId)
            .eq(EntityRelationEntity::getParentInd, BooleanTypeEnum.TRUE.getValue())
            .list();
    }

    /**
     * @see EntityRelationService#deleteAsAncestor(String, Long)
     */
    @Override
    public void deleteAsAncestor(String relationType, Long ancestorId) {
        lambdaUpdateWrapper()
            .eq(EntityRelationEntity::getRelationType, relationType)
            .eq(EntityRelationEntity::getAncestorId, ancestorId)
            .remove();
    }

    /**
     * @see EntityRelationService#deleteAsAncestor(String, List)
     */
    @Override
    public void deleteAsAncestor(String relationType, List<Long> ancestorIdList) {
        lambdaUpdateWrapper()
            .eq(EntityRelationEntity::getRelationType, relationType)
            .in(EntityRelationEntity::getAncestorId, ancestorIdList)
            .remove();
    }

    /**
     * @see EntityRelationService#deleteAsChild(String, Long)
     */
    @Override
    public void deleteAsChild(String relationType, Long entityId) {
        lambdaUpdateWrapper()
            .eq(EntityRelationEntity::getRelationType, relationType)
            .eq(EntityRelationEntity::getEntityId, entityId)
            .remove();
    }

    /**
     * @see EntityRelationService#deleteAsChild(String, List)
     */
    @Override
    public void deleteAsChild(String relationType, List<Long> entityIdList) {
        long count = lambdaQueryWrapper()
            .eq(EntityRelationEntity::getRelationType, relationType)
            .in(EntityRelationEntity::getEntityId, entityIdList)
            .count();

        if (count > 0) {
            lambdaUpdateWrapper()
                .eq(EntityRelationEntity::getRelationType, relationType)
                .in(EntityRelationEntity::getEntityId, entityIdList)
                .remove();
        }

    }

}
