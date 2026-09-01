package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.ai.domain.entity.AiToolEntity;
import cc.wdev.platform.system.ai.domain.request.AiToolGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiToolSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiToolVo;
import cc.wdev.platform.system.ai.enums.BaseAiToolBizTypeEnum;
import cc.wdev.platform.system.ai.service.AiToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiToolApiImpl implements AiToolApi {

    private final AiToolService aiToolService;

    /**
     * @see AiToolApi#initialize()
     */
    @Override
    public void initialize() {
        // 扫描枚举定义
        List<BaseAiToolBizTypeEnum> bizTypeEnumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseAiToolBizTypeEnum.class);

        // 待处理配置项实体
        List<AiToolEntity> updateEntityList = com.google.common.collect.Lists.newArrayList();
        List<AiToolEntity> insertEntityList = com.google.common.collect.Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(bizTypeEnumList)) {
            for (BaseAiToolBizTypeEnum bizTypeEnum : bizTypeEnumList) {
                AiToolEntity entity = this.aiToolService.findByCode(bizTypeEnum.getValue());
                if (entity != null) {
                    updateEntityList.add(entity);
                } else {
                    entity = new AiToolEntity();
                    entity.setCode(bizTypeEnum.getValue());
                    entity.setTitle(bizTypeEnum.getName());
                    entity.setToolName(bizTypeEnum.getToolName());
                    entity.setDescription(bizTypeEnum.getDescription());
                    insertEntityList.add(entity);
                }
            }
            this.aiToolService.insertBatch(insertEntityList);
            this.aiToolService.updateBatchById(updateEntityList);
        }
    }

    /**
     * @see AiToolApi#initialize()
     */
    @Override
    public Page<AiToolVo> findAiToolsPage(AiToolSearchRequest request) {
        return aiToolService.findByPage(request);
    }

    /**
     * @see AiToolApi#getAiTool(AiToolGetRequest)
     */
    @Override
    public AiToolVo getAiTool(AiToolGetRequest request) {
        return aiToolService.getAiTool(request);
    }

    /**
     * @see AiToolApi#saveAiTool(AiToolSaveRequest)
     */
    @Override
    public void saveAiTool(AiToolSaveRequest request) {
        aiToolService.saveAiTool(request);
    }

    /**
     * @see AiToolApi#getTools()
     */
    @Override
    public List<AiToolSimpleVo> getTools() {
        return aiToolService.getAiTools();
    }

}
