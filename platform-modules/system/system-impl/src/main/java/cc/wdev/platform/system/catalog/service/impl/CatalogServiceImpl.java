package cc.wdev.platform.system.catalog.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.catalog.domain.converter.CatalogConverter;
import cc.wdev.platform.system.catalog.domain.entity.CatalogEntity;
import cc.wdev.platform.system.catalog.domain.entity.CatalogRelationEntity;
import cc.wdev.platform.system.catalog.domain.request.CatalogDeleteRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogRelationSaveRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogSaveRequest;
import cc.wdev.platform.system.catalog.domain.vo.CatalogVo;
import cc.wdev.platform.system.catalog.enums.CatalogRelationBizTypeEnum;
import cc.wdev.platform.system.catalog.repository.CatalogRepository;
import cc.wdev.platform.system.catalog.service.CatalogRelationService;
import cc.wdev.platform.system.catalog.service.CatalogService;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class CatalogServiceImpl extends BaseCachingEntityService<CatalogEntity, Long, CatalogRepository> implements CatalogService {

    private final CacheKeyGenerator cacheKeyGenerator = SimpleTenantCacheKeyGenerator.builder().prefix(SystemCacheConstants.CATALOG).build();

    private final CatalogRelationService catalogRelationService;

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    @Override
    public CatalogVo saveCatalog(CatalogSaveRequest saveDto) {
        CatalogEntity entity = CatalogConverter.INSTANCE.saveDto2Entity(saveDto);

        if (saveDto.getParentId() != null && saveDto.getParentId() == 0L) {
            entity.setRootInd(BooleanTypeEnum.TRUE.getValue());
        }

        if (saveDto.getId() != null && saveDto.getId() > 0) {
            entity.setId(saveDto.getId());
        }

        //保存分类
        save(entity);

        //保存分类关系
        CatalogRelationSaveRequest relationSaveDto = CatalogRelationSaveRequest.builder()
            .entityId(entity.getId())
            .ancestorIdList(saveDto.getParentId() != null ? List.of(saveDto.getParentId()) : Collections.emptyList())
            .relationType(CatalogRelationBizTypeEnum.CATALOG_PARENT_CATALOG.getValue())
            .ancestorRelationType(CatalogRelationBizTypeEnum.CATALOG_PARENT_CATALOG.getValue())
            .build();

        this.catalogRelationService.saveCatalogRelation(relationSaveDto);

        return CatalogConverter.INSTANCE.entity2Vo(entity);
    }

    @Override
    public void deleteCatalog(CatalogDeleteRequest deleteDto) {
        if (deleteDto.getId() == null || deleteDto.getId() <= 0L) {
            return;
        }

        CatalogEntity entity = this.findById(deleteDto.getId());
        if (entity == null) {
            return;
        }

        //检查是否有子类
        List<CatalogRelationEntity> children = this.catalogRelationService.getChildren(CatalogRelationBizTypeEnum.CATALOG_PARENT_CATALOG.getValue(), deleteDto.getId());
        if (CollectionUtils.isNotEmpty(children)) {
            List<Long> childIds = children.stream().map(CatalogRelationEntity::getEntityId).toList();
            childIds.forEach(id -> {
                deleteCatalog(CatalogDeleteRequest.builder().id(id).build());
            });
        }

        //刪除分类
        softDeleteById(deleteDto.getId());

        //刪除分类关联
        this.catalogRelationService.deleteAsChild(CatalogRelationBizTypeEnum.CATALOG_PARENT_CATALOG.getValue(), deleteDto.getId());
        this.catalogRelationService.deleteAsAncestor(CatalogRelationBizTypeEnum.CATALOG_PARENT_CATALOG.getValue(), deleteDto.getId());
    }

    @Override
    public CatalogVo getRootCatalog(String bizType, Long bizId) {
        CatalogVo vo = CatalogConverter.INSTANCE.entity2Vo(lambdaQueryWrapper()
            .eq(CatalogEntity::getRootInd, BooleanTypeEnum.TRUE.getBoolValue())
            .eq(CatalogEntity::getBizType, bizType)
            .eq(CatalogEntity::getBizId, bizId)
            .one());
        if (vo == null) {
            return null;
        }

        getExtra(vo);
        return vo;
    }

    @Override
    public CatalogVo getCatalog(Long id) {
        CatalogEntity entity = findById(id);

        if (entity == null) {
            return null;
        }

        CatalogVo vo = CatalogConverter.INSTANCE.entity2Vo(entity);

        // 关联关系
        getExtra(vo);
        return vo;
    }

    @Override
    public Page<CatalogVo> getCatalogList(CatalogRequest request) {
        IPage<CatalogEntity> page = lambdaQueryWrapper()
            .eq(CatalogEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .like(request.getQ() != null, CatalogEntity::getTitle, request.getQ())
            .page(MyBatisPlusUtils.getMyBatisPlusPage(request.getPageable()));

        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage();
        }

        List<CatalogVo> voList = page.getRecords().stream().map(CatalogConverter.INSTANCE::entity2Vo).toList();
        voList.forEach(this::getExtra);

        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), voList, page.getTotal());
    }


    public void getExtra(CatalogVo vo) {
        List<CatalogRelationEntity> relations = this.catalogRelationService.getRelation(CatalogRelationBizTypeEnum.CATALOG_PARENT_CATALOG.getValue(), vo.getId());
        if (CollectionUtils.isNotEmpty(relations)) {
            RelationVo<CatalogRelationEntity> relationVo = RelationVo.<CatalogRelationEntity>builder()
                .bizType(vo.getBizType())
                .relationBizType(CatalogRelationBizTypeEnum.CATALOG_PARENT_CATALOG.getValue())
                .bizId(vo.getBizId())
                .ids(relations.stream().map(CatalogRelationEntity::getId).toArray(Long[]::new))
                .items(relations)
                .build();
            vo.setRelation(relationVo);
        }
    }
}
