package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.enums.EntityPackageBizTypeEnum;
import cc.wdev.platform.system.config.api.BizTypeApi;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import cc.wdev.platform.system.core.domain.entity.EntityPackageEntity;
import cc.wdev.platform.system.core.repository.EntityPackageRepository;
import cc.wdev.platform.system.core.service.EntityPackageService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.enums.ResponseCodeEnum.BIZ_TYPE__NOT_EMPTY;
import static cc.wdev.platform.commons.enums.ResponseCodeEnum.BIZ_TYPE__NOT_PRESENT;

/**
 * @author erden
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntityPackageServiceImpl extends BaseEntityService<EntityPackageEntity, Long, EntityPackageRepository> implements EntityPackageService {

    private final BizTypeApi bizTypeApi;

    private BizTypeVo<?> validBizType(EntityPackageBizTypeEnum bizTypeEnum) {
        if (bizTypeEnum == null) {
            throw new ServiceException(BIZ_TYPE__NOT_EMPTY);
        }

        BizTypeVo<?> bizType = BizTypeVo.builder()
            .bizType(bizTypeEnum.getValue())
            .build();
        if (bizType == null || StringUtils.isBlank(bizType.getBizType())) {
            log.error("Invalid Entity Package Biz Type: {}", bizTypeEnum);
            throw new ServiceException(BIZ_TYPE__NOT_PRESENT);
        }

        return bizType;
    }

    @Override
    public Set<Long> findPackageIds(EntityPackageBizTypeEnum bizTypeEnum, Long entityId) {
        if (!ObjectUtils.isValidId(entityId)) {
            return Collections.emptySet();
        }
        BizTypeVo<?> bizType = this.validBizType(bizTypeEnum);

        List<EntityPackageEntity> entities = this.lambdaQueryWrapper()
            .eq(EntityPackageEntity::getBizType, bizType.getBizType())
            .eq(EntityPackageEntity::getEntityId, entityId)
            .list();

        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptySet();
        }

        return entities.stream()
            .map(EntityPackageEntity::getPackageId)
            .collect(Collectors.toSet());
    }

    @Override
    public void deletePackage(EntityPackageBizTypeEnum bizTypeEnum, Long entityId) {
        BizTypeVo<?> bizType = this.validBizType(bizTypeEnum);

        this.lambdaUpdateWrapper()
            .eq(EntityPackageEntity::getBizType, bizType.getBizType())
            .eq(EntityPackageEntity::getEntityId, entityId)
            .remove();
    }

    @Override
    public void deletePackageByPackageId(Long packageId) {
        if (!ObjectUtils.isValidId(packageId)) {
            return;
        }
        this.lambdaUpdateWrapper()
            .eq(EntityPackageEntity::getPackageId, packageId)
            .remove();
    }

    @Override
    public void deletePackageByPackageIds(List<Long> packageIds) {
        if (CollectionUtils.isEmpty(packageIds)) {
            return;
        }
        this.lambdaUpdateWrapper()
            .in(EntityPackageEntity::getPackageId, packageIds)
            .remove();
    }

    @Override
    public boolean savePackage(EntityPackageBizTypeEnum bizTypeEnum, Long entityId, List<Long> packageIds) {
        Set<Long> beforePackageIds = this.findPackageIds(bizTypeEnum, entityId);
        Set<Long> afterPackageIds = Sets.newHashSet(Optional.ofNullable(packageIds)
            .orElse(Collections.emptyList()));
        if (afterPackageIds.equals(beforePackageIds)) {
            log.info("实体套餐未发生改变，不需要执行变更操作，bizType:{}， entityId:{}", bizTypeEnum.getLabelKey(), entityId);
            return false;
        }

        // 保存新的套餐关联
        List<EntityPackageEntity> entities = Lists.newArrayListWithCapacity(afterPackageIds.size());
        for (Long packageId : afterPackageIds) {
            EntityPackageEntity entity = EntityPackageEntity.builder()
                .entityId(entityId)
                .bizType(bizTypeEnum.getValue())
                .packageId(packageId)
                .build();
            entities.add(entity);
        }

        this.deletePackage(bizTypeEnum, entityId);

        if (CollectionUtils.isEmpty(entities)) {
            return true;
        }

        super.saveBatch(entities);
        return true;
    }

    @Override
    public Map<Long, List<Long>> packageIdsMap(List<Long> entityIds, EntityPackageBizTypeEnum bizTypeEnum) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return Collections.emptyMap();
        }

        List<EntityPackageEntity> entities = this.lambdaQueryWrapper()
            .select(EntityPackageEntity::getEntityId, EntityPackageEntity::getPackageId)
            .eq(EntityPackageEntity::getBizType, bizTypeEnum.getValue())
            .in(EntityPackageEntity::getEntityId, entityIds)
            .list();
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }

        Map<Long, List<Long>> packageIdsMap = Maps.newHashMapWithExpectedSize(entities.size());
        Map<Long, List<EntityPackageEntity>> packageEntitiesMap = entities.stream()
            .collect(Collectors.groupingBy(EntityPackageEntity::getEntityId));

        packageEntitiesMap.forEach((k, v) -> {
            if (CollectionUtils.isEmpty(v)) {
                return;
            }
            List<Long> packageIds = Lists.newArrayListWithCapacity(v.size());
            for (EntityPackageEntity packageEntity : v) {
                packageIds.add(packageEntity.getPackageId());
            }
            packageIdsMap.put(k, packageIds);
        });
        return packageIdsMap;
    }
}
