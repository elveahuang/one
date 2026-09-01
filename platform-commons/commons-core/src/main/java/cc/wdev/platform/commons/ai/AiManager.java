package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.advisor.SessionMetadataAdvisor;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.enums.AiVectorStoreType;
import cc.wdev.platform.commons.ai.factory.audio.SpeechModelFactory;
import cc.wdev.platform.commons.ai.factory.audio.TranscriptionModelFactory;
import cc.wdev.platform.commons.ai.factory.chat.ChatModelFactory;
import cc.wdev.platform.commons.ai.factory.embedding.EmbeddingModelFactory;
import cc.wdev.platform.commons.ai.factory.vectorstore.VectorStoreFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;

/**
 * @author elvea
 */
public interface AiManager {

    // ------------------------------------------------------------------------
    // 基础方法
    // ------------------------------------------------------------------------

    /**
     * 获取配置
     */
    AiConfig getConfig();

    /**
     * Spring AI Session - SessionService
     */
    SessionService getSessionService();

    /**
     * Spring AI Session - SessionMemoryAdvisor
     */
    SessionMemoryAdvisor getSessionMemoryAdvisor();

    /**
     * Spring AI Session - CustomSessionMetadataAdvisor
     */
    SessionMetadataAdvisor getSessionMetadataAdvisor();

    /**
     * QuestionAnswerAdvisor
     */
    QuestionAnswerAdvisor getQuestionAnswerAdvisor();

    /**
     * RetrievalAugmentationAdvisor
     */
    RetrievalAugmentationAdvisor getRetrievalAugmentationAdvisor();

    /**
     * ToolCallbackResolver
     */
    ObjectProvider<ToolCallbackResolver> getToolCallbackResolver();

    // ------------------------------------------------------------------------
    // 文本模型
    // ------------------------------------------------------------------------

    /**
     * 获取默认对话模型工厂
     */
    ChatModelFactory getChatModelFactory();

    /**
     * 获取指定对话模型工厂
     */
    ChatModelFactory getChatModelFactory(AiServiceProvider serviceProvider);

    /**
     * 获取指定对话模型工厂
     */
    ChatModelFactory getChatModelFactory(ModelConfig config);

    /**
     * 获取指定对话客户端
     */
    ChatClient getChatClient();

    /**
     * 获取指定对话客户端
     */
    ChatClient getChatClient(AiServiceProvider serviceProvider);

    /**
     * 获取指定对话客户端
     */
    ChatClient getChatClient(ModelConfig config);

    /**
     * 获取指定对话模型
     */
    ChatModel getChatModel();

    /**
     * 获取指定对话模型
     */
    ChatModel getChatModel(AiServiceProvider serviceProvider);

    /**
     * 获取指定对话模型
     */
    ChatModel getChatModel(ModelConfig config);

    // ------------------------------------------------------------------------
    // 向量模型
    // ------------------------------------------------------------------------

    /**
     * 获取默认向量模型工厂
     */
    EmbeddingModelFactory getEmbeddingModelFactory();

    /**
     * 获取指定向量模型工厂
     */
    EmbeddingModelFactory getEmbeddingModelFactory(ModelConfig config);

    /**
     * 获取指定向量模型工厂
     */
    EmbeddingModelFactory getEmbeddingModelFactory(AiServiceProvider serviceProvider);

    /**
     * 获取指定向量模型
     */
    EmbeddingModel getEmbeddingModel();

    /**
     * 获取指定向量模型
     */
    EmbeddingModel getEmbeddingModel(AiServiceProvider serviceProvider);

    /**
     * 获取指定向量模型
     */
    EmbeddingModel getEmbeddingModel(ModelConfig config);

    // ------------------------------------------------------------------------
    // 转录模型
    // ------------------------------------------------------------------------

    /**
     * 获取默认转录模型工厂
     */
    TranscriptionModelFactory getTranscriptionModelFactory();

    /**
     * 获取指定转录模型工厂
     */
    TranscriptionModelFactory getTranscriptionModelFactory(AiServiceProvider serviceProvider);

    /**
     * 获取指定转录模型工厂
     */
    TranscriptionModelFactory getTranscriptionModelFactory(ModelConfig config);

    // ------------------------------------------------------------------------
    // 转录模型
    // ------------------------------------------------------------------------

    /**
     * 获取默认转录模型工厂
     */
    SpeechModelFactory getSpeechModelFactory();

    /**
     * 获取指定转录模型工厂
     */
    SpeechModelFactory getSpeechModelFactory(AiServiceProvider serviceProvider);

    /**
     * 获取指定转录模型工厂
     */
    SpeechModelFactory getSpeechModelFactory(ModelConfig config);

    // ------------------------------------------------------------------------
    // Vector Store & RAG
    // ------------------------------------------------------------------------

    /**
     * 获取默认向量库工厂
     */
    VectorStoreFactory getVectorStoreFactory();

    /**
     * 获取指定类型向量库工厂
     */
    VectorStoreFactory getVectorStoreFactory(AiVectorStoreType storeType);

    /**
     * 获取指定向量存储
     */
    VectorStore getVectorStore();

    /**
     * 获取指定向量存储
     */
    VectorStore getVectorStore(String collectionName);

    /**
     * 获取指定向量存储
     */
    VectorStore getVectorStore(AiServiceProvider serviceProvider);

    /**
     * 获取指定向量存储
     */
    VectorStore getVectorStore(AiServiceProvider serviceProvider, String collectionName);

    /**
     * 获取指定向量存储
     */
    VectorStore getVectorStore(ModelConfig config);

    /**
     * 获取指定向量存储
     */
    VectorStore getVectorStore(ModelConfig config, String collectionName);

    /**
     * 获取文档切分器
     */
    TextSplitter getDocumentTransformer();

}
