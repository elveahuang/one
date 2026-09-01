package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.commons.enums.EntityRelationBizTypeEnum;
import cc.wdev.platform.system.core.domain.converter.PositionConverter;
import cc.wdev.platform.system.core.domain.dto.EntityRelationSaveDto;
import cc.wdev.platform.system.core.domain.dto.PositionDeleteDto;
import cc.wdev.platform.system.core.domain.dto.PositionDto;
import cc.wdev.platform.system.core.domain.dto.PositionSaveDto;
import cc.wdev.platform.system.core.domain.entity.PositionEntity;
import cc.wdev.platform.system.core.repository.PositionRepository;
import cc.wdev.platform.system.core.service.EntityRelationService;
import cc.wdev.platform.system.core.service.PositionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author elvea
 * @see PositionService
 * @see BaseCachingEntityService
 */
@Service
@AllArgsConstructor
public class PositionServiceImpl extends BaseCachingEntityService<PositionEntity, Long, PositionRepository>
    implements PositionService {

    private final CacheKeyGenerator cacheKeyGenerator = SimpleTenantCacheKeyGenerator.builder().prefix(SystemCacheConstants.POSITION).build();

    private final EntityRelationService entityRelationService;

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see PositionService#savePosition(PositionSaveDto)
     */
    @Override
    public PositionDto savePosition(PositionSaveDto saveDto) {
        PositionEntity entity = PositionConverter.INSTANCE.saveDto2Entity(saveDto);

        // 保存岗位基本信息
        this.save(entity);

        // 保存岗位关联信息
        EntityRelationSaveDto relationSaveDto = EntityRelationSaveDto.builder()
            .relationType(EntityRelationBizTypeEnum.ORG_PARENT_ORG.getValue())
            .ancestorIdList(Collections.singletonList(saveDto.getParentId()))
            .entityId(entity.getId())
            .build();
        this.entityRelationService.saveEntityRelation(relationSaveDto);

        return PositionConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see PositionService#deletePosition(PositionDeleteDto)
     */
    @Override
    public void deletePosition(PositionDeleteDto deleteDto) {
    }

    /**
     * @see PositionService#getRootPosition()
     */
    @Override
    public PositionEntity getRootPosition() {
        return lambdaQueryWrapper().eq(PositionEntity::getRootInd, BooleanTypeEnum.TRUE.getBoolValue()).one();
    }

    /**
     * @see PositionService#getDefaultPosition()
     */
    @Override
    public PositionEntity getDefaultPosition() {
        return lambdaQueryWrapper().eq(PositionEntity::getDefaultInd, BooleanTypeEnum.TRUE.getBoolValue()).one();
    }

}
