package cc.wdev.platform.system.config.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BaseBizTypeEnum;
import cc.wdev.platform.commons.enums.BizGroupTypeEnum;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.config.domain.entity.BizTypeEntity;
import cc.wdev.platform.system.config.domain.request.BizTypeDeleteRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSaveRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSearchRequest;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import cc.wdev.platform.system.config.repository.BizTypeRepository;
import cc.wdev.platform.system.config.service.BizTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@Service
public class BizTypeServiceImpl extends BaseCachingEntityService<BizTypeEntity, Long, BizTypeRepository> implements BizTypeService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(SystemCacheConstants.BIZ_TYPE);

    /**
     * @see CachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see BizTypeService#findBizTypePage(BizTypeSearchRequest)
     */
    @Override
    public <E> Page<BizTypeVo<E>> findBizTypePage(BizTypeSearchRequest request) {
        IPage<BizTypeEntity> page = this.lambdaQueryWrapper()
            .eq(BizTypeEntity::getBizGroupType, request.getBizGroupType())
            .eq(BizTypeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .like(StringUtils.isNotBlank(request.getQ()), BizTypeEntity::getBizType, request.getQ())
            .page(getMyBatisPlusPage(request.getPageable()));

        List<BizTypeVo<E>> bizTypeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            for (BizTypeEntity entity : page.getRecords()) {
                bizTypeList.add(toBizType(entity));
            }
        }
        return MyBatisPlusUtils.toSpringDataPage(page, bizTypeList);
    }

    /**
     * @see BizTypeService#findBizTypeList(BizTypeSearchRequest)
     */
    @Override
    public <E> List<BizTypeVo<E>> findBizTypeList(BizTypeSearchRequest request) {
        List<BizTypeEntity> entityList = findByGroup(request.getBizGroupType());

        List<BizTypeVo<E>> bizTypeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(entityList)) {
            for (BizTypeEntity entity : entityList) {
                bizTypeList.add(toBizType(entity));
            }
        }
        return bizTypeList;
    }

    /**
     * @see BizTypeService#findByPage(BizTypeSearchRequest)
     */
    @Override
    public Page<BizTypeEntity> findByPage(BizTypeSearchRequest request) {
        IPage<BizTypeEntity> page = this.lambdaQueryWrapper()
            .eq(BizTypeEntity::getBizGroupType, request.getBizGroupType())
            .eq(BizTypeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see BizTypeService#findByGroup(String)
     */
    @Override
    public List<BizTypeEntity> findByGroup(String bizTypeGroup) {
        return this.lambdaQueryWrapper()
            .eq(BizTypeEntity::getBizGroupType, bizTypeGroup)
            .in(BizTypeEntity::getBizScopeType, BizScopeTypeEnum.getBizScopeTypes())
            .eq(BizTypeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    /**
     * @see BizTypeService#getBizType(String, String, Class, Object)
     */
    @Override
    public <T, E extends BaseBizTypeEnum> BizTypeVo<T> getBizType(E bizTypeEnum, Class<T> configClass, T defaultConfig) {
        BizTypeEntity bizTypeEntity = getBizTypeEntity(bizTypeEnum.getGroup(), bizTypeEnum.getCode());

        BizTypeVo<T> bizType;
        if (bizTypeEntity == null) {
            bizType = BizTypeVo.<T>builder().bizType(bizTypeEnum.getCode()).bizGroupType(bizTypeEnum.getGroup()).build();
        } else {
            bizType = toBizType(bizTypeEntity);
        }
        // 设置业务类型配置
        setBizTypeConfig(bizType, configClass, defaultConfig);

        return bizType;
    }

    /**
     * @see BizTypeService#getBizType(String, String)
     */
    @Override
    public <T> BizTypeVo<T> getBizType(String bizGroupType, String bizType, Class<T> configClass, T defaultConfig) {
        bizGroupType = StringUtils.nvl(bizGroupType);
        bizType = StringUtils.nvl(bizType);
        BizTypeEntity bizTypeEntity = getBizTypeEntity(bizGroupType, bizType);

        BizTypeVo<T> bizTypeVo;
        if (bizTypeEntity == null) {
            bizTypeVo = BizTypeVo.<T>builder()
                .bizGroupType(BizGroupTypeEnum.NONE.getValue())
                .bizScopeType(BizScopeTypeEnum.NONE.getCode())
                .bizType(bizType)
                .build();
        } else {
            bizTypeVo = toBizType(bizTypeEntity);
        }

        // 设置业务类型配置
        setBizTypeConfig(bizTypeVo, configClass, defaultConfig);
        return bizTypeVo;
    }

    /**
     * @see BizTypeService#getBizTypeEntity(String, String)
     */
    @Override
    public BizTypeEntity getBizTypeEntity(String bizGroupType, String bizType) {
        LambdaQueryChainWrapper<BizTypeEntity> wrapper = this.lambdaQueryWrapper()
            .eq(BizTypeEntity::getBizGroupType, bizGroupType)
            .eq(BizTypeEntity::getBizType, bizType)
            .eq(BizTypeEntity::getActive, ActiveTypeEnum.ENABLED.getValue());
        return this.findOneByWrapper(wrapper);
    }

    /**
     * @see BizTypeService#saveBizType(BizTypeSaveRequest)
     */
    @Override
    public <T> void saveBizType(BizTypeSaveRequest<T> request) {
        if (StringUtils.isEmpty(request.getBizType()) || StringUtils.isEmpty(request.getBizGroupType())) {
            throw new IllegalArgumentException("bizTypeGroup and bizTypeCode are required");
        }

        BizTypeEntity bizTypeEntity = BizTypeEntity.builder()
            .bizGroupType(request.getBizGroupType())
            .bizType(request.getBizType())
            .description(request.getDescription())
            .extra(request.getExtra())
            .idx(request.getIdx())
            .status(request.getStatus())
            .build();
        bizTypeEntity.setActive(ActiveTypeEnum.ENABLED.getValue());

        if (request.getConfig() != null && !StringUtils.isEmpty(request.getExtra())) {
            try {
                bizTypeEntity.setExtra(JacksonUtils.toJson(request.getConfig()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        this.save(bizTypeEntity);
    }

    /**
     * @see BizTypeService#deleteBizType(BizTypeDeleteRequest)
     */
    @Override
    @Transactional
    public void deleteBizType(BizTypeDeleteRequest request) {
        if (StringUtils.isEmpty(request.getBizGroupType())) {
            return;
        }

        if (StringUtils.isEmpty(request.getBizType())
            && CollectionUtils.isEmpty(request.getBizTypeList())) {
            return;
        }

        String bizTypeCode = request.getBizType();
        List<String> codeList = request.getBizTypeList();
        List<BizTypeEntity> entities = Collections.emptyList();

        List<String> bizTypeCodeList = new ArrayList<>();
        if (StringUtils.isNotEmpty(bizTypeCode)) {
            bizTypeCodeList.add(bizTypeCode);
        }

        if (CollectionUtils.isNotEmpty(codeList)) {
            bizTypeCodeList.addAll(codeList);
        }

        entities = this.lambdaQueryWrapper()
            .eq(BizTypeEntity::getBizGroupType, request.getBizGroupType())
            .in(BizTypeEntity::getBizType, bizTypeCodeList)
            .eq(BizTypeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();

        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        this.softDeleteBatch(entities);
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(BizTypeEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(this.cacheKeyGenerator.byId(model.getId()), model);
            }
            if (StringUtils.isNotEmpty(model.getBizType())) {
                getCacheService().set(this.cacheKeyGenerator.byCode(model.getBizType()), model);
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(BizTypeEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(this.cacheKeyGenerator.byId(model.getId()));
            }
            if (StringUtils.isNotEmpty(model.getBizType())) {
                getCacheService().delete(this.cacheKeyGenerator.byCode(model.getBizType()));
            }
        }
    }

    private <C> void setBizTypeConfig(BizTypeVo<C> bizType, Class<C> configClass, C defaultConfig) {
        if (bizType == null) {
            return;
        }
        // 转换配置对象
        if (StringUtils.isNotEmpty(bizType.getExtra()) && configClass != null) {
            try {
                C config = JacksonUtils.toObject(bizType.getExtra(), configClass);

                // 默认配置不为空时，需要合并配置
                if (!ObjectUtils.isEmpty(defaultConfig)) {
                    BeanUtils.copyProperties(config, defaultConfig);
                } else {
                    defaultConfig = config;
                }

                bizType.setConfig(defaultConfig);
            } catch (Exception e) {
                log.error("Fail to convert bizType extra to config, bizType = {}", bizType, e);
                throw new RuntimeException(e);
            }
        } else if (defaultConfig != null) {
            bizType.setConfig(defaultConfig);
        }
    }

    private <C> BizTypeVo<C> toBizType(BizTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return BizTypeVo.<C>builder()
            .bizGroupType(entity.getBizGroupType())
            .bizScopeType(entity.getBizScopeType())
            .bizType(entity.getBizType())
            .title(entity.getTitle())
            .labelKey(entity.getLabelKey())
            .labelGroup(entity.getLabelGroup())
            .description(entity.getDescription())
            .extra(entity.getExtra())
            .idx(entity.getIdx())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

}
