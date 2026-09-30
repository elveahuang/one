package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.service.audio.TranscriptionModelService;
import cc.wdev.platform.commons.ai.service.chat.ChatModelService;
import cc.wdev.platform.commons.ai.service.embedding.EmbeddingModelService;
import cc.wdev.platform.commons.ai.service.image.ImageModelService;
import cc.wdev.platform.commons.ai.service.rerank.RerankModelService;

/**
 * @author elvea
 */
public interface AiServiceManager {

    // ------------------------------------------------------------------------
    // 基础方法
    // ------------------------------------------------------------------------

    /**
     * 获取配置
     */
    AiConfig getConfig();

    // ------------------------------------------------------------------------------
    // Chat
    // ------------------------------------------------------------------------------

    /**
     * 获取对话服务
     */
    ChatModelService getChatModelService();

    /**
     * 获取对话服务
     */
    ChatModelService getChatModelService(AiServiceProvider serviceProvider);

    /**
     * 获取对话服务
     */
    ChatModelService getChatModelService(ModelConfig modelConfig);

    // ------------------------------------------------------------------------------
    // Embedding
    // ------------------------------------------------------------------------------

    /**
     * 获取向量服务
     */
    EmbeddingModelService getEmbeddingModelService();

    /**
     * 获取向量服务
     */
    EmbeddingModelService getEmbeddingModelService(AiServiceProvider serviceProvider);

    /**
     * 获取向量服务
     */
    EmbeddingModelService getEmbeddingModelService(ModelConfig modelConfig);

    // ------------------------------------------------------------------------------
    // Image
    // ------------------------------------------------------------------------------

    /**
     * 获取图像服务
     */
    ImageModelService getImageService();

    /**
     * 获取图像服务
     */
    ImageModelService getImageService(AiServiceProvider serviceProvider);

    /**
     * 获取图像服务
     */
    ImageModelService getImageService(ModelConfig modelConfig);

    // ------------------------------------------------------------------------------
    // Audio
    // ------------------------------------------------------------------------------

    /**
     * 获取音频服务
     */
    TranscriptionModelService getTranscriptionModelService();

    /**
     * 获取音频服务
     */
    TranscriptionModelService getTranscriptionModelService(AiServiceProvider serviceProvider);

    /**
     * 获取音频服务
     */
    TranscriptionModelService getTranscriptionModelService(ModelConfig modelConfig);

    // ------------------------------------------------------------------------------
    // Rerank
    // ------------------------------------------------------------------------------

    /**
     * 获取重排服务
     */
    RerankModelService getRerankModelService();

    /**
     * 获取重排服务
     */
    RerankModelService getRerankModelService(AiServiceProvider serviceProvider);

    /**
     * 获取重排服务
     */
    RerankModelService getRerankModelService(ModelConfig modelConfig);

}
