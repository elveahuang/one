package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.ArrayUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.ai.domain.entity.AiAgentEntity;
import cc.wdev.platform.system.ai.domain.entity.AiRelationEntity;
import cc.wdev.platform.system.ai.domain.entity.AiToolEntity;
import cc.wdev.platform.system.ai.domain.request.AiAgentGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSearchRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelGetRequest;
import cc.wdev.platform.system.ai.domain.vo.AiAgentSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.ai.enums.AiRelationBizTypeEnum;
import cc.wdev.platform.system.ai.enums.BaseAiAgentBizTypeEnum;
import cc.wdev.platform.system.ai.service.*;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static cc.wdev.platform.commons.enums.ResponseCodeEnum.AI_INVALID_AGENT_MODEL;
import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class AiAgentApiImpl implements AiAgentApi {

    private final AiManager aiManager;

    private final AiAgentService aiAgentService;

    private final AiModelService aiModelService;

    private final AiToolService aiToolService;

    private final AiKbService aiKbService;

    private final AiRelationService aiRelationService;

    // ------------------------------------------------------------------------------
    // Base
    // ------------------------------------------------------------------------------

    /**
     * @see AiAgentApi#initialize()
     */
    @Override
    @Transactional
    public void initialize() {
        // 扫描枚举定义
        List<BaseAiAgentBizTypeEnum> bizTypeEnumList = cc.wdev.platform.commons.utils.ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseAiAgentBizTypeEnum.class);

        // 待处理配置项实体
        List<AiAgentEntity> updateEntityList = Lists.newArrayList();
        List<AiAgentEntity> insertEntityList = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(bizTypeEnumList)) {
            for (BaseAiAgentBizTypeEnum bizTypeEnum : bizTypeEnumList) {
                AiAgentEntity entity = this.aiAgentService.findByCode(bizTypeEnum.getValue());
                if (entity != null) {
                    updateEntityList.add(entity);
                } else {
                    entity = new AiAgentEntity();
                    insertEntityList.add(entity);
                }
                entity.setCode(bizTypeEnum.getValue());
                entity.setTitle(bizTypeEnum.getDescription());
                entity.setDescription(bizTypeEnum.getDescription());
                entity.setSystemPrompt(bizTypeEnum.getPrompt());
                entity.setStatus(StatusTypeEnum.ON.getValue());
                entity.setActive(ActiveTypeEnum.ENABLED.getValue());
            }
            this.aiAgentService.insertBatch(insertEntityList);
            this.aiAgentService.updateBatchById(updateEntityList);
        }
    }

    // ------------------------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------------------------

    /**
     * @see AiAgentApi#getAiAgent(AiAgentGetRequest)
     */
    @Override
    public @NonNull AiAgentVo getAiAgent(AiAgentGetRequest request) {
        AiAgentVo vo = this.aiAgentService.getAiAgent(request);
        getAiAgentExtra(vo);
        return vo;
    }

    /**
     * @see AiAgentApi#saveAiAgent(AiAgentSaveRequest)
     */
    @Override
    public void saveAiAgent(AiAgentSaveRequest request) {
        AiAgentEntity entity = aiAgentService.saveAiAgent(request);
        // 删除关联
        aiRelationService.deleteRelation(List.of(entity.getId()));
        // 保存关联
        aiRelationService.saveAgentRelation(entity.getId(), request.getModelId(), request.getKbId(), request.getToolIds(), request.getMcpServerIds());
    }

    /**
     * @see AiAgentApi#deleteAiAgent(DeleteRequest)
     */
    @Override
    public void deleteAiAgent(DeleteRequest request) {
        if (ArrayUtils.isEmpty(request.getIds())) {
            return;
        }

        List<Long> ids = Arrays.asList(request.getIds());
        aiAgentService.softDeleteBatchById(ids);
        aiRelationService.deleteRelation(ids);
    }

    /**
     * @see AiAgentApi#findAiAgents(AiAgentSearchRequest)
     */
    @Override
    public Page<AiAgentVo> findAiAgents(AiAgentSearchRequest request) {
        Page<AiAgentVo> page = aiAgentService.findByPage(request);
        page.getContent().forEach(this::getAiAgentExtra);
        return page;
    }

    // ------------------------------------------------------------------------------
    // Chat
    // ------------------------------------------------------------------------------

    /**
     * @see AiAgentApi#getAgents()
     */
    @Override
    public List<AiAgentSimpleVo> getAgents() {
        return this.aiAgentService.getAgents();
    }

    // ------------------------------------------------------------------------------
    // Chat
    // ------------------------------------------------------------------------------

    private void getAiAgentExtra(AiAgentVo aiAgentVo) {
        List<AiRelationEntity> relationList = aiRelationService.getRelation(aiAgentVo.getId());
        if (CollectionUtils.isEmpty(relationList)) {
            return;
        }

        List<Long> toolIds = Lists.newArrayList();
        List<Long> mcpServerIds = Lists.newArrayList();
        for (AiRelationEntity aiAgentRelation : relationList) {
            if (AiRelationBizTypeEnum.AGENT_CURRENT_MODEL.getValue().equals(aiAgentRelation.getBizType())) {
                aiAgentVo.setModelId(aiAgentRelation.getBizId());
            }
            if (AiRelationBizTypeEnum.AGENT_CURRENT_KB.getValue().equals(aiAgentRelation.getBizType())) {
                aiAgentVo.setKbId(aiAgentRelation.getBizId());
            }
            if (AiRelationBizTypeEnum.AGENT_CURRENT_TOOL.getValue().equals(aiAgentRelation.getBizType())) {
                toolIds.add(aiAgentRelation.getBizId());
            }
            if (AiRelationBizTypeEnum.AGENT_CURRENT_MCP.getValue().equals(aiAgentRelation.getBizType())) {
                mcpServerIds.add(aiAgentRelation.getBizId());
            }
        }

        // 获取模型详情
        if (ObjectUtils.isValidId(aiAgentVo.getModelId())) {
            AiModelVo aiModelVo = aiModelService.getAiModel(AiModelGetRequest.builder().id(aiAgentVo.getModelId()).build());
            if (aiModelVo == null) {
                throw new ServiceException(AI_INVALID_AGENT_MODEL);
            }
            aiAgentVo.setModel(aiModelVo);
        }

        // 获取知识库详情
        if (ObjectUtils.isValidId(aiAgentVo.getKbId())) {
            AiKbVo aiKbVo = aiKbService.getKb(GetRequest.builder().id(aiAgentVo.getKbId()).build());
            if (aiKbVo != null) {
                aiAgentVo.setKb(aiKbVo);
            }
        }

        // 获取工具名称
        if (CollectionUtils.isNotEmpty(toolIds)) {
            List<AiToolEntity> aiToolEntityList = this.aiToolService.findCacheByIds(toolIds);

            aiAgentVo.setToolIds(aiToolEntityList.stream().map(AiToolEntity::getId).toList());
            aiAgentVo.setToolNames(aiToolEntityList.stream().map(AiToolEntity::getToolName).toList());
        }

        // 获取MCP名称
        aiAgentVo.setMcpServerIds(mcpServerIds);
    }

}
