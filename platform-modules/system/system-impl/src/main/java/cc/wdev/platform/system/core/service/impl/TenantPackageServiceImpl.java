package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.SourceTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.enums.EntityAuthorityBizTypeEnum;
import cc.wdev.platform.system.commons.enums.PackageBizTypeEnum;
import cc.wdev.platform.system.core.cache.EntityAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.converter.PackageConverter;
import cc.wdev.platform.system.core.domain.entity.PackageEntity;
import cc.wdev.platform.system.core.domain.form.AddTenantPackageForm;
import cc.wdev.platform.system.core.domain.form.EditTenantPackageAuthorityForm;
import cc.wdev.platform.system.core.domain.form.EditTenantPackageForm;
import cc.wdev.platform.system.core.domain.request.TenantPackageSearchRequest;
import cc.wdev.platform.system.core.domain.vo.TenantPackageVo;
import cc.wdev.platform.system.core.repository.PackageRepository;
import cc.wdev.platform.system.core.service.EntityAuthorityService;
import cc.wdev.platform.system.core.service.EntityPackageService;
import cc.wdev.platform.system.core.service.TenantPackageService;
import cc.wdev.platform.system.storage.api.AttachmentApi;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import cc.wdev.platform.system.storage.enums.AttachmentBizTypeEnum;
import cc.wdev.platform.system.storage.enums.AttachmentRelationBizTypeEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 * @see TenantPackageService
 * @see BaseCachingEntityService
 */
@Service
@RequiredArgsConstructor
public class TenantPackageServiceImpl extends BaseEntityService<PackageEntity, Long, PackageRepository> implements TenantPackageService {

    public final static String TENANT_PACKAGE_CODE_PREFIX = "TENANT_PACKAGE";

    private final AttachmentApi attachmentApi;

    private final CacheService cacheService;

    private final EntityPackageService entityPackageService;

    private final EntityAuthorityService entityAuthorityService;

    @Override
    public Page<TenantPackageVo> findPackagePage(TenantPackageSearchRequest request) {
        IPage<PackageEntity> page = this.lambdaQueryWrapper()
            .eq(PackageEntity::getBizType, PackageBizTypeEnum.TENANT.getValue())
            .and(StringUtils.isNotEmpty(request.getQ()), wrapper ->
                wrapper.like(PackageEntity::getCode, request.getQ()).or().like(PackageEntity::getTitle, request.getQ()))
            .eq(PackageEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(PackageEntity::getIdx)
            .page(getMyBatisPlusPage(request.getPageable()));

        List<PackageEntity> entities = page.getRecords();
        if (CollectionUtils.isEmpty(entities)) {
            return Page.empty(request.getPageable());
        }

        IPage<TenantPackageVo> iPage = getMyBatisPlusPage(request);
        List<TenantPackageVo> vos = this.buildTenantPackageVoList(entities);
        iPage.setRecords(vos);
        iPage.setTotal(page.getTotal());

        return MyBatisPlusUtils.toSpringDataPage(iPage);
    }

    private List<TenantPackageVo> buildTenantPackageVoList(List<PackageEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        List<Long> entityIds = Lists.newArrayListWithCapacity(entities.size());
        for (PackageEntity entity : entities) {
            entityIds.add(entity.getId());
        }

        List<TenantPackageVo> vos = Lists.newArrayListWithCapacity(entities.size());

        // 批量获取封面附件
        Map<Long, AttachmentVo> coverMap = attachmentApi.getAttachmentBatch(AttachmentRequest.builder()
            .bizType(AttachmentRelationBizTypeEnum.TENANT_PACKAGE_COVER.getValue())
            .bizIdList(entityIds)
            .relationBizType(AttachmentRelationBizTypeEnum.TENANT_PACKAGE_COVER.getValue())
            .build());
        for (PackageEntity entity : entities) {
            TenantPackageVo vo = PackageConverter.INSTANCE.entityToTenantPackageVo(entity);
            vo.setCover(coverMap.get(entity.getId()));
            vos.add(vo);
        }

        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPackage(AddTenantPackageForm form) {
        PackageEntity entity = PackageConverter.INSTANCE.addFormToEntity(form);
        if (StringUtils.isBlank(form.getCode())) {
            entity.setCode(generateCode(TENANT_PACKAGE_CODE_PREFIX));
        }
        entity.setActive(1);
        entity.setSource(SourceTypeEnum.NORMAL.getValue());
        entity.setBizType(PackageBizTypeEnum.TENANT.getValue());
        this.save(entity);

        // 保存关联
        this.saveRelation(entity, form);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPackage(EditTenantPackageForm form) {
        PackageEntity entity = this.checkExist(form.getId());

        if (StringUtils.isBlank(form.getCode())) {
            form.setCode(generateCode(TENANT_PACKAGE_CODE_PREFIX));
        }
        ObjectUtils.copyNotNullProperties(form, entity);
        entity.setSource(SourceTypeEnum.NORMAL.getValue());
        entity.setBizType(PackageBizTypeEnum.TENANT.getValue());

        // 保存内容
        this.save(entity);
        // 保存关联
        this.saveRelation(entity, form);
    }

    private <E extends AddTenantPackageForm> void saveRelation(PackageEntity entity, E form) {
        if (!ObjectUtils.isValidId(entity)) {
            return;
        }
        // 保存封面附件关联
        attachmentApi.saveAttachmentRelation(AttachmentRelationRequest.builder()
            .bizType(AttachmentBizTypeEnum.TENANT_PACKAGE_COVER.getValue())
            .bizId(entity.getId())
            .relationBizType(AttachmentRelationBizTypeEnum.TENANT_PACKAGE_COVER.getValue())
            .attachmentIdList(Optional.ofNullable(form.getCover()).map(AttachmentVo::getIds).orElse(Collections.emptyList()))
            .build()
        );
    }

    private PackageEntity checkExist(Long id) {
        if (!ObjectUtils.isValidId(id)) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }
        PackageEntity entity = this.findById(id);
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.TENANT__PACKAGE_NOT_PRESENT);
        }
        return entity;
    }

    @Override
    public TenantPackageVo findPackageById(Long id) {
        PackageEntity entity = this.checkExist(id);
        return PackageConverter.INSTANCE.entityToTenantPackageVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPackageAuthority(EditTenantPackageAuthorityForm form) {
        PackageEntity entity = this.checkExist(form.getPackageId());
        entityAuthorityService.saveAuthority(EntityAuthorityBizTypeEnum.PACKAGE, entity.getId(), form.getAuthorityIds());

        // 清除相关的套餐权限缓存
        CacheKey cacheKey = EntityAuthorityCacheKeyGenerator.keyByEntity(EntityAuthorityBizTypeEnum.PACKAGE.getValue(), entity.getId());
        cacheService.delete(cacheKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePackage(Long packageId) {
        PackageEntity entity = this.checkExist(packageId);
        // 删除套餐
        super.delete(entity);

        // 删除关联数据，如套餐权限表
        entityPackageService.deletePackageByPackageId(packageId);
        entityAuthorityService.deleteAuthority(EntityAuthorityBizTypeEnum.PACKAGE, packageId);

        // 清除相关的套餐权限缓存
        CacheKey cacheKey = EntityAuthorityCacheKeyGenerator
            .keyByEntity(EntityAuthorityBizTypeEnum.PACKAGE.getValue(), packageId);
        cacheService.delete(cacheKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePackage(List<Long> packageIds) {
        if (CollectionUtils.isEmpty(packageIds)) {
            return;
        }

        super.deleteBatchById(packageIds);

        entityPackageService.deletePackageByPackageIds(packageIds);
        entityAuthorityService.deleteAuthority(EntityAuthorityBizTypeEnum.PACKAGE, packageIds);

        // 清除相关的套餐权限缓存
        List<String> cacheKeys = EntityAuthorityCacheKeyGenerator
            .keysByBizIds(EntityAuthorityBizTypeEnum.PACKAGE.getValue(), packageIds);
        cacheService.delete(cacheKeys);
    }

    @Override
    public Set<Long> findAuthorityIdsByPackageId(Long packageId) {
        if (!ObjectUtils.isValidId(packageId)) {
            return Collections.emptySet();
        }

        return entityAuthorityService.findAuthorityIds(EntityAuthorityBizTypeEnum.PACKAGE, packageId);
    }

    @Override
    public List<TenantPackageVo> list() {
        List<PackageEntity> list = this.lambdaQueryWrapper()
            .eq(PackageEntity::getBizType, PackageBizTypeEnum.TENANT.getValue())
            .orderByAsc(PackageEntity::getIdx)
            .list();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<TenantPackageVo> vos = Lists.newArrayListWithCapacity(list.size());
        for (PackageEntity packageEntity : list) {
            vos.add(PackageConverter.INSTANCE.entityToTenantPackageVo(packageEntity));
        }
        return vos;
    }
}
