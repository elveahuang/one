package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiModelSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 模型接口
 *
 * @author elvea
 */
public interface AiModelApi {

    // ------------------------------------------------------------------------------
    // Base
    // ------------------------------------------------------------------------------

    /**
     * 初始化系统模型
     */
    void initialize();

    // ------------------------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------------------------

    /**
     * 获取模型
     */
    AiModelVo getAiModel(AiModelGetRequest request);

    /**
     * 保存模型
     */
    void saveAiModel(AiModelSaveRequest request);

    /**
     * 删除模型
     */
    void deleteAiModel(DeleteRequest request);

    /**
     * 分页查询模型
     */
    Page<AiModelVo> findAiModels(AiModelSearchRequest request);

    // ------------------------------------------------------------------------------
    // Chat
    // ------------------------------------------------------------------------------

    /**
     * 获取可用模型
     */
    List<AiModelSimpleVo> getModels();

    /**
     * 获取请求客户端
     */
    ChatClient getChatClient(SimpleChatRequest request);

}
