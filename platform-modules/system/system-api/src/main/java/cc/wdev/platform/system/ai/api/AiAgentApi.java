package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.system.ai.domain.request.AiAgentGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiAgentSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 智能体接口
 *
 * @author elvea
 */
public interface AiAgentApi {

    // ------------------------------------------------------------------------------
    // Base
    // ------------------------------------------------------------------------------

    /**
     * 初始化系统智能体
     */
    void initialize();

    // ------------------------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------------------------

    /**
     * 获取智能体
     */
    AiAgentVo getAiAgent(AiAgentGetRequest request);

    /**
     * 更新智能体
     */
    void saveAiAgent(AiAgentSaveRequest request);

    /**
     * 删除智能体
     */
    void deleteAiAgent(DeleteRequest request);

    /**
     * 分页查询智能体
     */
    Page<AiAgentVo> findAiAgents(AiAgentSearchRequest request);

    // ------------------------------------------------------------------------------
    // Chat
    // ------------------------------------------------------------------------------

    /**
     * 获取可用智能体
     */
    List<AiAgentSimpleVo> getAgents();

}
