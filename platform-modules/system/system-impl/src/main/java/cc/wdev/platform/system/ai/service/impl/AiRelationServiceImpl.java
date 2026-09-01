package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiRelationEntity;
import cc.wdev.platform.system.ai.enums.AiRelationBizTypeEnum;
import cc.wdev.platform.system.ai.repository.AiRelationRepository;
import cc.wdev.platform.system.ai.service.AiRelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRelationServiceImpl extends BaseEntityService<AiRelationEntity, Long, AiRelationRepository> implements AiRelationService {

    /**
     * @see AiRelationService#saveAgentRelation(Long, Long, Long, List, List)
     */
    @Override
    public void saveAgentRelation(Long aiAgentId, Long modeId, Long kbId, List<Long> toolIds, List<Long> mcpServerIds) {
        List<AiRelationEntity> entities = Lists.newArrayList();

        if (ObjectUtils.isValidId(modeId)) {
            entities.add(AiRelationEntity.builder()
                .entityId(aiAgentId)
                .bizId(modeId)
                .bizType(AiRelationBizTypeEnum.AGENT_CURRENT_MODEL.getValue())
                .build()
            );
        }

        if (ObjectUtils.isValidId(kbId)) {
            entities.add(AiRelationEntity.builder()
                .entityId(aiAgentId)
                .bizId(kbId)
                .bizType(AiRelationBizTypeEnum.AGENT_CURRENT_KB.getValue())
                .build()
            );
        }

        if (CollectionUtils.isNotEmpty(toolIds)) {
            entities.addAll(toolIds.stream().map((id) -> AiRelationEntity.builder()
                .entityId(aiAgentId)
                .bizId(id)
                .bizType(AiRelationBizTypeEnum.AGENT_CURRENT_TOOL.getValue())
                .build()).toList());
        }

        if (CollectionUtils.isNotEmpty(mcpServerIds)) {
            entities.addAll(mcpServerIds.stream().map((id) -> AiRelationEntity.builder()
                .entityId(aiAgentId)
                .bizId(id)
                .bizType(AiRelationBizTypeEnum.AGENT_CURRENT_MCP.getValue())
                .build()).toList());
        }
        this.saveBatch(entities);
    }

    /**
     * @see AiRelationService#saveKbRelation(Long, Long, Long, Long)
     */
    @Override
    public void saveKbRelation(Long aiKbId, Long embeddingModelId, Long chatModelId, Long rerankModelId) {
        List<AiRelationEntity> entities = Lists.newArrayList();

        // 向量模型
        if (ObjectUtils.isValidId(embeddingModelId)) {
            entities.add(AiRelationEntity.builder()
                .entityId(aiKbId)
                .bizId(embeddingModelId)
                .bizType(AiRelationBizTypeEnum.KB_CURRENT_EMBEDDING_MODEL.getValue())
                .build()
            );
        }

        // 对话模型
        if (ObjectUtils.isValidId(chatModelId)) {
            entities.add(AiRelationEntity.builder()
                .entityId(aiKbId)
                .bizId(chatModelId)
                .bizType(AiRelationBizTypeEnum.KB_CURRENT_CHAT_MODEL.getValue())
                .build()
            );
        }

        // 重排模型
        if (ObjectUtils.isValidId(rerankModelId)) {
            entities.add(AiRelationEntity.builder()
                .entityId(aiKbId)
                .bizId(rerankModelId)
                .bizType(AiRelationBizTypeEnum.KB_CURRENT_RERANK_MODEL.getValue())
                .build()
            );
        }

        this.saveBatch(entities);
    }

    /**
     * @see AiRelationService#deleteRelation(List)
     */
    @Override
    public void deleteRelation(List<Long> entityIds) {
        this.deleteRelation(entityIds, null);
    }

    /**
     * @see AiRelationService#getRelation(Long, String)
     */
    @Override
    public void deleteRelation(List<Long> entityIds, String relationType) {
        if (CollectionUtils.isNotEmpty(entityIds)) {
            lambdaUpdateWrapper()
                .in(AiRelationEntity::getEntityId, entityIds)
                .eq(StringUtils.isNotEmpty(relationType), AiRelationEntity::getBizType, relationType)
                .remove();
        }
    }

    /**
     * @see AiRelationService#getRelation(Long, String)
     */
    @Override
    public List<AiRelationEntity> getRelation(Long entityId) {
        return this.getRelation(entityId, null);
    }

    /**
     * @see AiRelationService#getRelation(Long, String)
     */
    @Override
    public List<AiRelationEntity> getRelation(Long entityId, String relationType) {
        if (ObjectUtils.isValidId(entityId)) {
            return this.lambdaQueryWrapper()
                .eq(AiRelationEntity::getEntityId, entityId)
                .eq(StringUtils.isNotEmpty(relationType), AiRelationEntity::getBizType, relationType)
                .eq(AiRelationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .list();
        }
        return List.of();
    }

}
