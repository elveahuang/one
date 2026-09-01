package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiModelEntity;
import cc.wdev.platform.system.ai.domain.request.AiModelGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiModelSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AiModelService extends CachingEntityService<AiModelEntity, Long> {

    /**
     * 根据编号或者ID获取模型，编号优先级高
     */
    AiModelVo getAiModel(AiModelGetRequest request);

    /**
     * 保存模型
     */
    AiModelEntity saveAiModel(AiModelSaveRequest request);

    /**
     * 分页查询模型
     */
    Page<AiModelVo> findByPage(AiModelSearchRequest request);

    /**
     * 获取模型
     */
    List<AiModelSimpleVo> getAiModels();

}
