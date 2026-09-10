package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.system.ai.domain.request.AiToolSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiToolSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiToolVo;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AiToolApi {

    /**
     * 初始化系统智能体
     */
    void initialize();

    /**
     * 工具列表分页查询
     */
    Page<AiToolVo> findAiToolsPage(AiToolSearchRequest request);

    /**
     * 获取工具
     */
    AiToolVo getAiTool(GetRequest request);

    /**
     * 保存工具
     */
    void saveAiTool(AiToolSaveRequest request);

    /**
     * 获取所有工具
     */
    List<AiToolSimpleVo> getTools();

}
