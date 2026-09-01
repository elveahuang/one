package cc.wdev.platform.system.catalog.service.impl;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.catalog.domain.entity.CatalogRelationEntity;
import cc.wdev.platform.system.catalog.domain.request.CatalogRelationSaveRequest;
import cc.wdev.platform.system.catalog.repository.CatalogRelationRepository;
import cc.wdev.platform.system.catalog.service.CatalogRelationService;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.core.domain.dto.EntityRelationSaveDto;
import cc.wdev.platform.system.core.service.EntityRelationService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author elvea
 */
@Slf4j
@Service
public class CatalogRelationServiceImpl extends BaseCachingEntityService<CatalogRelationEntity, Long, CatalogRelationRepository> implements CatalogRelationService {

    private final CacheKeyGenerator cacheKeyGenerator = SimpleTenantCacheKeyGenerator.builder().prefix(SystemCacheConstants.CATALOG_RELATION).build();

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see EntityRelationService#saveEntityRelation(EntityRelationSaveDto)
     */
    @Override
    public void saveCatalogRelation(CatalogRelationSaveRequest saveDto) {
        List<CatalogRelationEntity> relationList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(saveDto.getAncestorIdList())) {
            for (Long ancestorId : saveDto.getAncestorIdList()) {
                AtomicInteger index = new AtomicInteger(1);
                List<CatalogRelationEntity> entityRelationList = Lists.newArrayList();

                StringBuilder sb = new StringBuilder();
                List<CatalogRelationEntity> ancestorRelationList = this.getParents(saveDto.getAncestorRelationType(), ancestorId);
                // 增加直接上级的上级关联
                if (CollectionUtils.isNotEmpty(ancestorRelationList)) {
                    entityRelationList.addAll(ancestorRelationList.stream().map(r -> {
                        // 关联路径
                        sb.append(GlobalConstants.DELIMITER).append(r.getAncestorId());
                        // 构建关联
                        return CatalogRelationEntity.builder()
                            .ancestorId(r.getAncestorId())
                            .entityId(saveDto.getEntityId())
                            .parentInd(BooleanTypeEnum.FALSE.getValue())
                            .relationType(saveDto.getRelationType())
                            .idx(index.getAndIncrement())
                            .build();
                    }).toList());
                }

                // 增加直接上级的关联
                entityRelationList.add(CatalogRelationEntity.builder()
                    .ancestorId(ancestorId)
                    .entityId(saveDto.getEntityId())
                    .parentInd(BooleanTypeEnum.TRUE.getValue())
                    .relationType(saveDto.getRelationType())
                    .idx(index.getAndIncrement())
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

    @Override
    public List<CatalogRelationEntity> getRelation(String relationType, Long entityId) {
        return lambdaQueryWrapper()
            .eq(CatalogRelationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(CatalogRelationEntity::getRelationType, relationType)
            .eq(CatalogRelationEntity::getEntityId, entityId)
            .list();
    }

    @Override
    public List<CatalogRelationEntity> getParents(String relationType, Long entityId) {
        return lambdaQueryWrapper()
            .eq(CatalogRelationEntity::getRelationType, relationType)
            .eq(CatalogRelationEntity::getEntityId, entityId)
            .list();
    }

    @Override
    public List<CatalogRelationEntity> getDirectParents(String relationType, Long entityId) {
        return lambdaQueryWrapper()
            .eq(CatalogRelationEntity::getRelationType, relationType)
            .eq(CatalogRelationEntity::getEntityId, entityId)
            .eq(CatalogRelationEntity::getParentInd, BooleanTypeEnum.TRUE.getBoolValue())
            .list();
    }

    @Override
    public List<CatalogRelationEntity> getChildren(String relationType, Long ancestorId) {
        return lambdaQueryWrapper()
            .eq(CatalogRelationEntity::getRelationType, relationType)
            .eq(CatalogRelationEntity::getAncestorId, ancestorId)
            .list();
    }

    @Override
    public List<CatalogRelationEntity> getDirectChildren(String relationType, Long ancestorId) {
        return lambdaQueryWrapper()
            .eq(CatalogRelationEntity::getRelationType, relationType)
            .eq(CatalogRelationEntity::getAncestorId, ancestorId)
            .eq(CatalogRelationEntity::getParentInd, BooleanTypeEnum.TRUE.getBoolValue())
            .list();
    }

    @Override
    public void deleteAsAncestor(String relationType, Long ancestorId) {
        lambdaUpdateWrapper()
            .eq(CatalogRelationEntity::getRelationType, relationType)
            .eq(CatalogRelationEntity::getAncestorId, ancestorId)
            .remove();
    }

    @Override
    public void deleteAsAncestor(String relationType, List<Long> ancestorIdList) {
        lambdaUpdateWrapper()
            .eq(CatalogRelationEntity::getRelationType, relationType)
            .in(CatalogRelationEntity::getAncestorId, ancestorIdList)
            .remove();
    }

    @Override
    public void deleteAsChild(String relationType, Long entityId) {
        lambdaUpdateWrapper()
            .eq(CatalogRelationEntity::getRelationType, relationType)
            .eq(CatalogRelationEntity::getEntityId, entityId)
            .remove();
    }

    @Override
    public void deleteAsChild(String relationType, List<Long> entityIdList) {
        long count = lambdaQueryWrapper().eq(CatalogRelationEntity::getRelationType, relationType)
            .in(CatalogRelationEntity::getEntityId, entityIdList)
            .count();

        if (count > 0) {
            lambdaUpdateWrapper().eq(CatalogRelationEntity::getRelationType, relationType)
                .in(CatalogRelationEntity::getEntityId, entityIdList)
                .remove();
        }
    }

}
