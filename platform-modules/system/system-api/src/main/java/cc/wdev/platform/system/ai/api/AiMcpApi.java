package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.domain.DeleteRequest;
import cc.wdev.platform.commons.domain.GetRequest;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiMcpServerVo;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AiMcpApi {

    /**
     * 分页查询MCP服务
     */
    Page<AiMcpServerVo> findAiMcpServers(AiMcpServerSearchRequest request);

    /**
     * 获取MCP服务
     */
    AiMcpServerVo getAiMcpServer(GetRequest request);

    /**
     * 保存MCP服务
     */
    void saveAiMcpServer(AiMcpServerSaveRequest request);

    /**
     * 删除MCP服务
     */
    void deleteAiMcpServer(DeleteRequest request);

    /**
     * 为指定MCP服务构建工具回调提供者；没有可用服务时返回 null
     */
    ToolCallbackProvider buildToolCallbackProvider(List<Long> mcpServerIds);

}
