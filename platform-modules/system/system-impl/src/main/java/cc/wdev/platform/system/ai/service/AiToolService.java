package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiToolEntity;
import cc.wdev.platform.system.ai.domain.request.AiToolGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiToolSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiToolVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author dev
 */
public interface AiToolService extends CachingEntityService<AiToolEntity, Long> {

    /**
     * 工具列表分页查询
     */
    Page<AiToolVo> findByPage(AiToolSearchRequest request);

    /**
     * 获取工具
     */
    AiToolVo getAiTool(AiToolGetRequest request);

    /**
     * 保存工具
     */
    void saveAiTool(AiToolSaveRequest request);

    /**
     * 获取所有工具
     */
    List<AiToolSimpleVo> getAiTools();

}
