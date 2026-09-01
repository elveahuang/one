package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiAgentEntity;
import cc.wdev.platform.system.ai.domain.request.AiAgentGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiAgentSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AiAgentService extends CachingEntityService<AiAgentEntity, Long> {

    /**
     * 根据编号或者ID获取智能体，编号优先级高
     */
    AiAgentVo getAiAgent(AiAgentGetRequest request);

    /**
     * 保存智能体
     */
    AiAgentEntity saveAiAgent(AiAgentSaveRequest request);

    /**
     * 分页查询智能体
     */
    Page<AiAgentVo> findByPage(AiAgentSearchRequest request);

    /**
     * 获取可用智能体
     */
    List<AiAgentSimpleVo> getAgents();

}
