package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.ai.utils.AiSecretUtils;
import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiModelConverter;
import cc.wdev.platform.system.ai.domain.entity.AiModelEntity;
import cc.wdev.platform.system.ai.domain.request.AiModelGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiModelSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.ai.repository.AiModelRepository;
import cc.wdev.platform.system.ai.service.AiModelService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.AI_MODEL;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiModelServiceImpl
    extends BaseCachingEntityService<AiModelEntity, Long, AiModelRepository>
    implements AiModelService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(AI_MODEL);

    /**
     * @see CachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see AiModelService#getAiModel(AiModelGetRequest)
     */
    @Override
    public AiModelVo getAiModel(AiModelGetRequest request) {
        AiModelEntity entity = null;
        if (StringUtils.isNotEmpty(request.getCode())) {
            entity = this.findCacheByCode(request.getCode().trim());
        } else if (ObjectUtils.isValidId(request.getId())) {
            entity = this.findCacheById(request.getId());
        }
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.AI_INVALID_MODEL);
        }
        return AiModelConverter.INSTANCE.entityVo(entity);
    }

    /**
     * @see AiModelService#findByPage(AiModelSearchRequest)
     */
    @Override
    public Page<AiModelVo> findByPage(AiModelSearchRequest request) {
        IPage<AiModelEntity> page = this.lambdaQueryWrapper()
            .eq(StringUtils.isNotBlank(request.getModelProvider()), AiModelEntity::getModelProvider, request.getModelProvider())
            .eq(StringUtils.isNotBlank(request.getServiceProvider()), AiModelEntity::getServiceProvider, request.getServiceProvider())
            .eq(StringUtils.isNotBlank(request.getModelType()), AiModelEntity::getModelType, request.getModelType())
            .like(StringUtils.isNotBlank(request.getQ()), AiModelEntity::getModelName, request.getQ())
            .eq(AiModelEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(request.getPageable()));
        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<AiModelVo> vos = page.getRecords().stream().map(AiModelConverter.INSTANCE::entityVo).toList();
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }

    /**
     * @see AiModelService#saveAiModel(AiModelSaveRequest)
     */
    @Override
    public AiModelEntity saveAiModel(AiModelSaveRequest request) {
        AiModelEntity entity = AiModelConverter.INSTANCE.form2Entity(request);
        if (ObjectUtils.isValidId(request.getId())) {
            entity.setId(request.getId());
            if (StringUtils.isNotEmpty(request.getApiKey()) && request.getApiKey().contains("****")) {
                entity.setApiKey(null);
            }
        }
        // 密钥加密落库（含新增与更新场景）
        if (StringUtils.isNotEmpty(entity.getApiKey())) {
            entity.setApiKey(AiSecretUtils.encrypt(entity.getApiKey()));
        }
        return this.save(entity);
    }

    /**
     * @see AiModelService#getAiModels()
     */
    @Override
    public List<AiModelSimpleVo> getAiModels() {
        List<AiModelEntity> entities = this.lambdaQueryWrapper()
            .select(AiModelEntity::getId, AiModelEntity::getCode, AiModelEntity::getTitle, AiModelEntity::getServiceProvider, AiModelEntity::getModelProvider, AiModelEntity::getModelName, AiModelEntity::getModelType)
            .eq(AiModelEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AiModelEntity::getStatus, StatusTypeEnum.ON.getValue())
            .list();

        return entities.stream().map(e -> AiModelSimpleVo.builder()
            .id(e.getId())
            .code(e.getCode())
            .title(e.getTitle())
            .modelName(e.getModelName())
            .modelType(e.getModelType())
            .modelProvider(e.getModelProvider())
            .serviceProvider(e.getServiceProvider())
            .build()
        ).toList();
    }

}
