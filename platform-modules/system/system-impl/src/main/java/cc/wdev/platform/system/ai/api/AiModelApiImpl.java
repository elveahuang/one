package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.model.SimpleModelConfig;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.ai.domain.entity.AiModelEntity;
import cc.wdev.platform.system.ai.domain.request.AiModelGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiModelSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.ai.enums.BaseAiModelBizTypeEnum;
import cc.wdev.platform.system.ai.service.AiModelService;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static cc.wdev.platform.commons.enums.ResponseCodeEnum.AI_INVALID_MODEL;
import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class AiModelApiImpl implements AiModelApi {

    private final AiModelService aiModelService;

    private final AiManager aiManager;

    // ------------------------------------------------------------------------------
    // Base
    // ------------------------------------------------------------------------------

    /**
     * @see AiModelApi#initialize()
     */
    @Override
    @Transactional
    public void initialize() {
        // 扫描枚举定义
        List<BaseAiModelBizTypeEnum> bizTypeEnumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseAiModelBizTypeEnum.class);

        // 待处理配置项实体
        List<AiModelEntity> updateEntityList = Lists.newArrayList();
        List<AiModelEntity> insertEntityList = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(bizTypeEnumList)) {
            for (BaseAiModelBizTypeEnum bizTypeEnum : bizTypeEnumList) {
                AiModelEntity entity = this.aiModelService.findByCode(bizTypeEnum.getValue());
                if (entity != null) {
                    updateEntityList.add(entity);
                } else {
                    entity = new AiModelEntity();
                    insertEntityList.add(entity);
                }
                entity.setCode(bizTypeEnum.getValue());
                entity.setTitle(bizTypeEnum.getModelName());
                entity.setDescription(bizTypeEnum.getDescription());
                entity.setModelName(bizTypeEnum.getModelName());
                entity.setServiceProvider(bizTypeEnum.getServiceProvider());
                entity.setModelProvider(bizTypeEnum.getModelProvider());
                entity.setModelType(bizTypeEnum.getModelType());
                entity.setStatus(StatusTypeEnum.OFF.getValue());
                entity.setActive(ActiveTypeEnum.ENABLED.getValue());
            }
            this.aiModelService.insertBatch(insertEntityList);
            this.aiModelService.updateBatchById(updateEntityList);
        }
    }

    // ------------------------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------------------------

    /**
     * @see AiModelApi#findAiModels(AiModelSearchRequest)
     */
    @Override
    public Page<AiModelVo> findAiModels(AiModelSearchRequest request) {
        return aiModelService.findByPage(request);
    }

    /**
     * @see AiModelApi#saveAiModel(AiModelSaveRequest)
     */
    @Override
    public void saveAiModel(AiModelSaveRequest request) {
        if (null == request) {
            return;
        }
        aiModelService.saveAiModel(request);
    }

    /**
     * @see AiModelApi#deleteAiModel(DeleteRequest)
     */
    @Override
    public void deleteAiModel(DeleteRequest request) {
        if (!CollectionUtils.isEmpty(Arrays.asList(request.getIds()))) {
            this.aiModelService.softDeleteBatchById(Arrays.asList(request.getIds()));
        }
    }

    /**
     * @see AiModelApi#getAiModel(AiModelGetRequest)
     */
    @Override
    public AiModelVo getAiModel(AiModelGetRequest request) {
        return aiModelService.getAiModel(request);
    }

    // ------------------------------------------------------------------------------
    // Chat
    // ------------------------------------------------------------------------------

    /**
     * @see AiModelApi#getModels()
     */
    @Override
    public List<AiModelSimpleVo> getModels() {
        return aiModelService.getAiModels();
    }

    /**
     * @see AiModelApi#getChatClient(SimpleChatRequest)
     */
    @Override
    public ChatClient getChatClient(SimpleChatRequest request) {
        // 查询大模型
        AiModelVo aiModel = this.aiModelService.getAiModel(AiModelGetRequest.builder()
            .id(request.getModelId())
            .code(request.getModelCode())
            .build()
        );

        SimpleModelConfig config = SimpleModelConfig.builder()
            .name(aiModel.getModelName())
            .modelType(aiModel.getModelType())
            .modelProvider(aiModel.getModelProvider())
            .serviceProvider(aiModel.getServiceProvider())
            .baseUrl(aiModel.getBaseUrl())
            .apiKey(aiModel.getApiKey())
            .build();
        ChatClient chatClient = this.aiManager.getChatModelFactory(config).getChatClient(config);
        if (null == chatClient) {
            throw new ServiceException(AI_INVALID_MODEL);
        }
        return chatClient;
    }

}
