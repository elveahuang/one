package cc.wdev.platform.system.config.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BizGroupTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.config.api.BizTypeApi;
import cc.wdev.platform.system.config.domain.converter.ConfigConverter;
import cc.wdev.platform.system.config.domain.entity.ConfigEntity;
import cc.wdev.platform.system.config.domain.request.ConfigGetRequest;
import cc.wdev.platform.system.config.domain.request.ConfigSaveRequest;
import cc.wdev.platform.system.config.domain.request.ConfigSearchRequest;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import cc.wdev.platform.system.config.domain.vo.ConfigVo;
import cc.wdev.platform.system.config.repository.ConfigRepository;
import cc.wdev.platform.system.config.service.ConfigService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.toSpringDataPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.CONFIG;

/**
 * @author elvea
 * @see ConfigService
 * @see BaseCachingEntityService
 */
@Service
@AllArgsConstructor
public class ConfigServiceImpl extends BaseCachingEntityService<ConfigEntity, Long, ConfigRepository> implements ConfigService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(CONFIG);

    private final BizTypeApi bizTypeApi;

    /**
     * @see CachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see ConfigService#getConfigEntity(ConfigGetRequest)
     */
    @Override
    public ConfigEntity getConfigEntity(ConfigGetRequest form) {
        return this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(ConfigEntity::getTenantId, form.getTenantId())
            .eq(ConfigEntity::getConfigKey, form.getConfigKey())
        );
    }

    /**
     * @see ConfigService#saveConfig(ConfigSaveRequest)
     */
    @Override
    public void saveConfig(ConfigSaveRequest form) {
        BizTypeVo<?> bizType = this.bizTypeApi.getBizType(BizGroupTypeEnum.CONFIG_TYPE.getValue(), form.getConfigKey());

        String bizScopeTypeStr = bizType.getBizScopeType();

        if (BizScopeTypeEnum.PLATFORM.getCode().equals(bizScopeTypeStr)) {
            CacheKey cacheKey = cacheKeyGenerator.key(form.getConfigKey());
            ConfigEntity entity = getCacheService().get(cacheKey, k -> this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(ConfigEntity::getTenantId, 0L)
                .eq(ConfigEntity::getConfigKey, form.getConfigKey())
                .eq(ConfigEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            ));

            // 配置项不存在，则创建新的配置项
            if (entity == null) {
                entity = ConfigEntity.builder()
                    .configKey(form.getConfigKey())
                    .build();
                entity.setTenantId(0L);
            }
            entity.setConfigValue(form.getConfigValue());
            entity.setDescription(form.getDescription());
            this.save(entity);

        } else if (BizScopeTypeEnum.SYSTEM.getCode().equals(bizScopeTypeStr)) {
            CacheKey cacheKey = cacheKeyGenerator.key(TenantContext.getTenantId(), form.getConfigKey());
            ConfigEntity entity = getCacheService().get(cacheKey, k -> this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(ConfigEntity::getTenantId, TenantContext.getTenantId())
                .eq(ConfigEntity::getConfigKey, form.getConfigKey())
                .eq(ConfigEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            ));

            // 配置项不存在，则创建新的配置项
            if (entity == null) {
                entity = ConfigEntity.builder()
                    .configKey(form.getConfigKey())
                    .build();
                entity.setTenantId(TenantContext.getTenantId());
            }
            entity.setConfigValue(form.getConfigValue());
            entity.setDescription(form.getDescription());
            this.save(entity);
        }
    }

    /**
     * @see ConfigService#getConfig(String)
     */
    @Override
    public ConfigVo getConfig(String configKey) {
        BizTypeVo<?> bizType = this.bizTypeApi.getBizType(BizGroupTypeEnum.CONFIG_TYPE.getValue(), configKey);
        String bizScopeTypeStr = bizType.getBizScopeType();

        // 根据 scope 字符串判断是 SYSTEM 还是 TENANT
        if (BizScopeTypeEnum.PLATFORM.getCode().equals(bizScopeTypeStr)) {
            CacheKey cacheKey = cacheKeyGenerator.key(configKey);
            ConfigEntity entity = getCacheService().get(cacheKey, k -> this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(ConfigEntity::getTenantId, 0L)
                .eq(ConfigEntity::getConfigKey, configKey)
                .eq(ConfigEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            ));
            if (entity != null) {
                return ConfigConverter.INSTANCE.entityToDto(entity);
            }
        } else if (BizScopeTypeEnum.SYSTEM.getCode().equals(bizScopeTypeStr)) {
            CacheKey cacheKey = cacheKeyGenerator.key(TenantContext.getTenantId(), configKey);
            ConfigEntity entity = getCacheService().get(cacheKey, k -> this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(ConfigEntity::getTenantId, TenantContext.getTenantId())
                .eq(ConfigEntity::getConfigKey, configKey)
                .eq(ConfigEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            ));
            if (entity != null) {
                return ConfigConverter.INSTANCE.entityToDto(entity);
            }
        }
        return null;
    }

    @Override
    public Page<ConfigEntity> findByPage(ConfigSearchRequest searchRequest) {
        List<Long> tenantIds = Lists.newArrayList(TenantContext.getTenantId());
        if (BooleanTypeEnum.getTrueValue() == TenantContext.getTenantRootInd()) {
            tenantIds.add(0L);
        }
        LambdaQueryChainWrapper<ConfigEntity> wrapper = this.lambdaQueryWrapper()
            .in(ConfigEntity::getTenantId, tenantIds)
            .eq(ConfigEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .like(searchRequest.getQ() != null, ConfigEntity::getConfigKey, searchRequest.getQ())
            .orderByDesc(ConfigEntity::getUpdatedAt);
        IPage<ConfigEntity> page = this.findPageByWrapper(getMyBatisPlusPage(searchRequest.getPageable()), wrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<ConfigEntity> list = page.getRecords().stream().toList();
            return toSpringDataPage(page, list);
        }
        return SpringDataUtils.emptyPage(searchRequest.getPageable());
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(ConfigEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(this.cacheKeyGenerator.byId(model.getId()), model);
            }
            if (StringUtils.isNotEmpty(model.getConfigKey())) {
                if (model.getTenantId() != null && model.getTenantId() > 0) {
                    // TENANT 级配置：CONFIG:tenantId:configKey
                    getCacheService().set(this.cacheKeyGenerator.key(model.getTenantId(), model.getConfigKey()), model);
                } else {
                    // SYSTEM 级配置：CONFIG:configKey
                    getCacheService().set(this.cacheKeyGenerator.key(model.getConfigKey()), model);
                }
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(ConfigEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(this.cacheKeyGenerator.byId(model.getId()));
            }
            if (StringUtils.isNotEmpty(model.getConfigKey())) {
                if (model.getTenantId() != null && model.getTenantId() > 0) {
                    getCacheService().delete(this.cacheKeyGenerator.key(model.getTenantId(), model.getConfigKey()));
                } else {
                    getCacheService().delete(this.cacheKeyGenerator.key(model.getConfigKey()));
                }
            }
        }
    }

}
