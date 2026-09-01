package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.commons.enums.EntityRelationBizTypeEnum;
import cc.wdev.platform.system.core.domain.converter.OrganizationConverter;
import cc.wdev.platform.system.core.domain.dto.EntityRelationSaveDto;
import cc.wdev.platform.system.core.domain.dto.OrganizationDeleteDto;
import cc.wdev.platform.system.core.domain.dto.OrganizationDto;
import cc.wdev.platform.system.core.domain.dto.OrganizationSaveDto;
import cc.wdev.platform.system.core.domain.entity.OrganizationEntity;
import cc.wdev.platform.system.core.repository.OrganizationRepository;
import cc.wdev.platform.system.core.service.EntityRelationService;
import cc.wdev.platform.system.core.service.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author elvea
 * @see OrganizationService
 * @see BaseCachingEntityService
 */
@Service
@AllArgsConstructor
public class OrganizationServiceImpl extends BaseCachingEntityService<OrganizationEntity, Long, OrganizationRepository>
    implements OrganizationService {

    private final CacheKeyGenerator cacheKeyGenerator = SimpleTenantCacheKeyGenerator.builder().prefix(SystemCacheConstants.ORGANIZATION).build();

    private final EntityRelationService entityRelationService;

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see OrganizationService#saveOrganization(OrganizationSaveDto)
     */
    @Override
    public OrganizationDto saveOrganization(OrganizationSaveDto saveDto) {
        OrganizationEntity entity = OrganizationConverter.INSTANCE.saveDto2Entity(saveDto);

        // 保存部门基本信息
        this.save(entity);

        // 保存部门关联信息
        EntityRelationSaveDto relationSaveDto = EntityRelationSaveDto.builder()
            .relationType(EntityRelationBizTypeEnum.ORG_PARENT_ORG.getValue())
            .ancestorIdList(Collections.singletonList(saveDto.getParentId()))
            .entityId(entity.getId())
            .build();
        this.entityRelationService.saveEntityRelation(relationSaveDto);

        return OrganizationConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see OrganizationService#deleteOrganization(OrganizationDeleteDto)
     */
    @Override
    public void deleteOrganization(OrganizationDeleteDto deleteDto) {
    }

    /**
     * @see OrganizationService#getRootOrganization()
     */
    @Override
    public OrganizationEntity getRootOrganization() {
        return lambdaQueryWrapper().eq(OrganizationEntity::getRootInd, BooleanTypeEnum.TRUE.getBoolValue()).one();
    }

    /**
     * @see OrganizationService#getDefaultOrganization()
     */
    @Override
    public OrganizationEntity getDefaultOrganization() {
        return lambdaQueryWrapper().eq(OrganizationEntity::getDefaultInd, BooleanTypeEnum.TRUE.getBoolValue()).one();
    }

}
