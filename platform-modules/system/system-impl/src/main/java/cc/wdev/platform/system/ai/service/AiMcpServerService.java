package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiMcpServerEntity;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiMcpServerVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author dev
 */
public interface AiMcpServerService extends CachingEntityService<AiMcpServerEntity, Long> {
    /**
     * 根据id值获取MCP服务列表
     */
    AiMcpServerVo getAiMcpServer(Long id);

    /**
     * 根据code值获取MCP服务列表
     */
    AiMcpServerVo getAiMcpServerByCode(String code);

    /**
     * 删除MCP服务
     */
    void deleteAiMcpServer(List<Long> ids);

    /**
     * 保存MCP服务
     */
    void saveAiMcpServer(AiMcpServerSaveRequest request);

    /**
     * 根据查询条件分页查询MCP服务
     */
    Page<AiMcpServerVo> findByPage(AiMcpServerSearchRequest request);

}
