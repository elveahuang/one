package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.enums.EntityAuthorityBizTypeEnum;
import cc.wdev.platform.system.commons.enums.EntityPackageBizTypeEnum;
import cc.wdev.platform.system.core.cache.UserAuthorityCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.converter.TenantConverter;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.domain.form.AddTenantForm;
import cc.wdev.platform.system.core.domain.form.BindTenantPackageForm;
import cc.wdev.platform.system.core.domain.form.EditTenantForm;
import cc.wdev.platform.system.core.domain.request.TenantSearchRequest;
import cc.wdev.platform.system.core.domain.vo.TenantVo;
import cc.wdev.platform.system.core.repository.TenantRepository;
import cc.wdev.platform.system.core.service.EntityAuthorityService;
import cc.wdev.platform.system.core.service.EntityPackageService;
import cc.wdev.platform.system.core.service.TenantService;
import cc.wdev.platform.system.storage.api.AttachmentApi;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import cc.wdev.platform.system.storage.enums.AttachmentBizTypeEnum;
import cc.wdev.platform.system.storage.enums.AttachmentRelationBizTypeEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.TENANT;

/**
 * @author erden
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends BaseCachingEntityService<TenantEntity, Long, TenantRepository> implements TenantService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleCacheKeyGenerator(TENANT);

    public static final String TENANT_CODE_PREFIX = "TENANT";

    private final AttachmentApi attachmentApi;

    private final EntityPackageService entityPackageService;

    private final EntityAuthorityService entityAuthorityService;

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    /**
     * @see TenantService#findByCode(String)
     */
    @Override
    public TenantEntity findByCode(String code) {
        return getCacheService().get(getCacheKeyGenerator().byCode(code), _ -> lambdaQueryWrapper()
            .eq(TenantEntity::getCode, code)
            .one()
        );
    }

    /**
     * @see TenantService#findTenantPage(TenantSearchRequest)
     */
    @Override
    public Page<TenantVo> findTenantPage(TenantSearchRequest request) {
        IPage<TenantEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(request.getQ()), wrapper -> wrapper
                .like(TenantEntity::getCode, request.getQ())
                .or().like(TenantEntity::getTitle, request.getQ()))
            .eq(TenantEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(TenantEntity::getRootInd, BooleanTypeEnum.FALSE.getValue()) // 重点排除顶层租户
            .orderByDesc(TenantEntity::getExpirationDate)
            .page(getMyBatisPlusPage(request.getPageable()));

        return MyBatisPlusUtils.toSpringDataPage(page, this.buildTenantPackageVoList(page.getRecords()));
    }

    private List<TenantVo> buildTenantPackageVoList(List<TenantEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        List<Long> entityIds = Lists.newArrayListWithCapacity(entities.size());
        List<TenantVo> vos = Lists.newArrayListWithCapacity(entities.size());
        for (TenantEntity entity : entities) {
            entityIds.add(entity.getId());
        }

        // 批量获取封面附件
        Map<Long, AttachmentVo> coverMap = attachmentApi.getAttachmentBatch(AttachmentRequest.builder()
            .bizType(AttachmentRelationBizTypeEnum.TENANT_PACKAGE_COVER.getValue())
            .bizIdList(entityIds)
            .relationBizType(AttachmentRelationBizTypeEnum.TENANT_PACKAGE_COVER.getValue())
            .build());
        // 批量获取关联套餐
        Map<Long, List<Long>> packageIdsMap = entityPackageService.packageIdsMap(entityIds, EntityPackageBizTypeEnum.TENANT);

        for (TenantEntity entity : entities) {
            TenantVo vo = TenantConverter.INSTANCE.entity2Vo(entity);
            vo.setCover(coverMap.get(entity.getId()));
            vo.setPackageIds(packageIdsMap.get(entity.getId()));
            vos.add(vo);
        }

        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTenant(AddTenantForm form) {
        TenantEntity entity = TenantConverter.INSTANCE.addFormToEntity(form);
        if (StringUtils.isBlank(form.getCode())) {
            entity.setCode(generateCode(TENANT_CODE_PREFIX));
        }
        entity.setSource(cc.wdev.platform.commons.enums.SourceTypeEnum.NORMAL.getValue());
        entity.setActive(1);
        entity.setRegistrationDate(LocalDateTime.now());

        this.save(entity);
        // 保存关联
        this.saveRelation(entity, form);
        // todo 初始化一套租户权限
    }

    @Override
    public void editTenant(EditTenantForm form) {
        TenantEntity entity = this.checkExist(form.getId());

        if (StringUtils.isBlank(form.getCode())) {
            form.setCode(generateCode(TENANT_CODE_PREFIX));
        }
        ObjectUtils.copyNotNullProperties(form, entity);

        // 保存内容
        this.save(entity);
        // 保存关联
        this.saveRelation(entity, form);
    }

    private TenantEntity checkExist(Long id) {
        if (!ObjectUtils.isValidId(id)) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }
        TenantEntity entity = this.findById(id);
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.TENANT__NOT_PRESENT);
        }
        return entity;
    }

    private <E extends AddTenantForm> void saveRelation(TenantEntity entity, E form) {
        if (!ObjectUtils.isValidId(entity)) {
            return;
        }

        // 保存封面附件关联
        attachmentApi.saveAttachmentRelation(AttachmentRelationRequest.builder()
            .bizType(AttachmentBizTypeEnum.TENANT_COVER.getValue())
            .bizId(entity.getId())
            .relationBizType(AttachmentRelationBizTypeEnum.TENANT_COVER.getValue())
            .attachmentIdList(Optional.ofNullable(form.getCover()).map(AttachmentVo::getIds).orElse(Collections.emptyList()))
            .build()
        );
    }

    @Override
    public TenantVo findTenantById(Long tenantId) {
        TenantEntity entity = this.checkExist(tenantId);
        return TenantConverter.INSTANCE.entity2Vo(entity);
    }

    @Override
    public TenantEntity findByDomain(String domain) {
        return getCacheService().get(getCacheKeyGenerator().byCode(domain), _ -> lambdaQueryWrapper()
            .eq(TenantEntity::getDomain, domain)
            .one()
        );
    }

    @Override
    public void deleteTenant(Long tenantId) {
        TenantEntity entity = this.checkExist(tenantId);
        // 删除租户
        this.delete(entity);

        // 删除关联数据
        entityPackageService.deletePackage(EntityPackageBizTypeEnum.TENANT, tenantId);
        entityAuthorityService.deleteAuthority(EntityAuthorityBizTypeEnum.TENANT, tenantId);
    }

    @Override
    public void batchDeleteTenant(List<Long> tenantIds) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPackage(BindTenantPackageForm form) {
        TenantEntity entity = this.checkExist(form.getTenantId());
        entityPackageService.savePackage(EntityPackageBizTypeEnum.TENANT, entity.getId(), form.getPackageIds());
    }

    @Override
    public void syncPackage(Long tenantId) {
        TenantEntity entity = this.checkExist(tenantId);

        Set<Long> packageIds = entityPackageService.findPackageIds(EntityPackageBizTypeEnum.TENANT, entity.getId());
        Set<Long> authorityIds = entityAuthorityService.findAuthorityIds(EntityAuthorityBizTypeEnum.PACKAGE, packageIds);
        if (CollectionUtils.isEmpty(authorityIds)) {
            return;
        }

        boolean authChangeInd = entityAuthorityService.saveAuthority(EntityAuthorityBizTypeEnum.TENANT, entity.getId(), authorityIds);
        if (authChangeInd) {
            // 清除租户相关用户的权限缓存
            getCacheService().deleteByPattern(UserAuthorityCacheKeyGenerator.keyPattern(tenantId));
        }
    }

    @Override
    public void enableTenant(Long tenantId) {
        TenantEntity entity = this.checkExist(tenantId);

        TenantEntity update = new TenantEntity();
        update.setId(entity.getId());
        update.setStatus(StatusTypeEnum.ON.getValue());

        this.save(update);
    }

    @Override
    public void disableTenant(Long tenantId) {
        TenantEntity entity = this.checkExist(tenantId);

        TenantEntity update = new TenantEntity();
        update.setId(entity.getId());
        update.setStatus(StatusTypeEnum.OFF.getValue());

        this.save(update);
    }

    @Override
    public void updateExpirationDate(Long tenantId, LocalDateTime expirationDate) {
        TenantEntity entity = this.checkExist(tenantId);

        TenantEntity update = new TenantEntity();
        update.setId(entity.getId());
        update.setExpirationDate(expirationDate);

        this.save(update);
    }

    @Override
    public List<TenantEntity> findAll() {
        return this.lambdaQueryWrapper()
            .eq(TenantEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    @Override
    public long getAllTenantCount(LocalDateTime startTime, LocalDateTime endTime) {
        return this.lambdaQueryWrapper().eq(TenantEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .gt(TenantEntity::getCreatedAt, startTime)
            .lt(TenantEntity::getCreatedAt, endTime)
            .count();
    }

    @Override
    public List<TenantEntity> filter() {
        return this.lambdaQueryWrapper().eq(TenantEntity::getActive, ActiveTypeEnum.ENABLED.getValue()).list();
    }

}
