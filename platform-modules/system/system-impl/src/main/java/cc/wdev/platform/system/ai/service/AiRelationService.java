package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.ai.domain.entity.AiRelationEntity;

import java.util.List;

/**
 * @author dev
 */
public interface AiRelationService extends EntityService<AiRelationEntity, Long> {

    /**
     * 保存智能体关联
     */
    void saveAgentRelation(Long aiAgentId, Long modelId, Long kbId, List<Long> toolIds, List<Long> mcpServerIds);

    /**
     * 保存知识库关联
     */
    void saveKbRelation(Long aiKbId, Long embeddingModelId, Long chatModelId, Long rerankModelId);

    /**
     * 删除关联
     */
    void deleteRelation(List<Long> entityIds);

    /**
     * 删除关联
     */
    void deleteRelation(List<Long> entityIds, String relationType);

    /**
     * 获取关联
     */
    List<AiRelationEntity> getRelation(Long entityIds);

    /**
     * 获取关联
     */
    List<AiRelationEntity> getRelation(Long entityIds, String relationType);

}
