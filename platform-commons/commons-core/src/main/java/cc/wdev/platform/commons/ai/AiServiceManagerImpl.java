package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.ai.service.ModelService;
import cc.wdev.platform.commons.ai.service.audio.TranscriptionModelService;
import cc.wdev.platform.commons.ai.service.chat.ChatModelService;
import cc.wdev.platform.commons.ai.service.embedding.EmbeddingModelService;
import cc.wdev.platform.commons.ai.service.image.ImageModelService;
import cc.wdev.platform.commons.ai.service.rerank.RerankModelService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class AiServiceManagerImpl implements AiServiceManager {

    private final List<ChatModelService> chatModelServices = new ArrayList<>();

    private final List<EmbeddingModelService> embeddingModelServices = new ArrayList<>();

    private final List<ImageModelService> imageModelServices = new ArrayList<>();

    private final List<TranscriptionModelService> transcriptionModelServices = new ArrayList<>();

    private final List<RerankModelService> rerankModelServices = new ArrayList<>();

    private final AiConfig config;

    private final List<ModelService> modelServices;

    @PostConstruct
    public void init() {
        log.info("ModelServics Registry");

        if (CollectionUtils.isNotEmpty(modelServices)) {
            for (ModelService service : modelServices) {
                if (service instanceof ChatModelService modelService) {
                    this.chatModelServices.add(modelService);
                }
                if (service instanceof EmbeddingModelService modelService) {
                    this.embeddingModelServices.add(modelService);
                }
                if (service instanceof ImageModelService modelService) {
                    this.imageModelServices.add(modelService);
                }
                if (service instanceof TranscriptionModelService modelService) {
                    this.transcriptionModelServices.add(modelService);
                }
                if (service instanceof RerankModelService modelService) {
                    this.rerankModelServices.add(modelService);
                }
            }
        }

        log.info("ModelServics. total : {}", chatModelServices.size());
        log.info("ChatModelService. total : {}", modelServices.size());
        log.info("EmbeddingModelService. total : {}", embeddingModelServices.size());
        log.info("ImageModelService. total : {}", imageModelServices.size());
        log.info("TranscriptionModelService. total : {}", transcriptionModelServices.size());
        log.info("RerankModelService. total : {}", rerankModelServices.size());
    }

    // ------------------------------------------------------------------------
    // 基础方法
    // ------------------------------------------------------------------------

    /**
     * @see AiServiceManager#getConfig()
     */
    @Override
    public AiConfig getConfig() {
        return this.config;
    }

    // ------------------------------------------------------------------------------
    // Chat
    // ------------------------------------------------------------------------------

    /**
     * @see AiServiceManager#getChatModelService()
     */
    @Override
    public ChatModelService getChatModelService() {
        return getChatModelService(AiServiceProvider.getChatServiceProvider(config.getService().getText()));
    }

    /**
     * @see AiServiceManager#getChatModelService(AiServiceProvider)
     */
    @Override
    public ChatModelService getChatModelService(AiServiceProvider serviceProvider) {
        return this.chatModelServices.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable ChatModelService."));
    }

    /**
     * @see AiServiceManager#getChatModelService(ModelConfig)
     */
    @Override
    public ChatModelService getChatModelService(ModelConfig modelConfig) {
        if (StringUtils.isEmpty(modelConfig.getServiceProvider())) {
            throw new IllegalArgumentException("ChatModelService - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(modelConfig.getModelProvider())) {
            throw new IllegalArgumentException("ChatModelService - modelProvider cannot be null");
        }
        if (modelConfig.getName() == null) {
            throw new IllegalArgumentException("ChatModelService - modelName cannot be null");
        }

        ChatModelService service = getChatModelService(AiServiceProvider.getChatServiceProvider(modelConfig.getServiceProvider()));
        if (service.supports(modelConfig)) {
            return service;
        }
        return this.getChatModelService();
    }

    // ------------------------------------------------------------------------------
    // Embedding
    // ------------------------------------------------------------------------------

    /**
     * @see AiServiceManager#getEmbeddingModelService()
     */
    @Override
    public EmbeddingModelService getEmbeddingModelService() {
        return getEmbeddingModelService(AiServiceProvider.getEmbeddingServiceProvider(config.getService().getEmbedding()));
    }

    /**
     * @see AiServiceManager#getEmbeddingModelService(AiServiceProvider)
     */
    @Override
    public EmbeddingModelService getEmbeddingModelService(AiServiceProvider serviceProvider) {
        return this.embeddingModelServices.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable EmbeddingModelService."));
    }

    /**
     * @see AiServiceManager#getEmbeddingModelService(ModelConfig)
     */
    @Override
    public EmbeddingModelService getEmbeddingModelService(ModelConfig modelConfig) {
        if (StringUtils.isEmpty(modelConfig.getServiceProvider())) {
            throw new IllegalArgumentException("EmbeddingModelService - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(modelConfig.getModelProvider())) {
            throw new IllegalArgumentException("EmbeddingModelService - modelProvider cannot be null");
        }
        if (modelConfig.getName() == null) {
            throw new IllegalArgumentException("EmbeddingModelService - modelName cannot be null");
        }

        EmbeddingModelService service = getEmbeddingModelService(AiServiceProvider.getEmbeddingServiceProvider(modelConfig.getServiceProvider()));
        if (service.supports(modelConfig)) {
            return service;
        }
        return this.getEmbeddingModelService();
    }

    // ------------------------------------------------------------------------------
    // Image
    // ------------------------------------------------------------------------------

    /**
     * @see AiServiceManager#getImageService()
     */
    @Override
    public ImageModelService getImageService() {
        return getImageService(AiServiceProvider.getImageServiceProvider(config.getService().getImage()));
    }

    /**
     * @see AiServiceManager#getImageService(AiServiceProvider)
     */
    @Override
    public ImageModelService getImageService(AiServiceProvider serviceProvider) {
        return this.imageModelServices.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable ImageModelService."));
    }

    /**
     * @see AiServiceManager#getImageService(ModelConfig)
     */
    @Override
    public ImageModelService getImageService(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("ImageModelService - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(config.getModelProvider())) {
            throw new IllegalArgumentException("ImageModelService - modelProvider cannot be null");
        }
        if (config.getName() == null) {
            throw new IllegalArgumentException("ImageModelService - modelName cannot be null");
        }

        ImageModelService service = getImageService(AiServiceProvider.getImageServiceProvider(config.getServiceProvider()));
        if (service.supports(config)) {
            return service;
        }
        return this.getImageService();
    }


    // ------------------------------------------------------------------------------
    // Audio
    // ------------------------------------------------------------------------------

    /**
     * @see AiServiceManager#getTranscriptionModelService()
     */
    @Override
    public TranscriptionModelService getTranscriptionModelService() {
        return getTranscriptionModelService(AiServiceProvider.getTranscriptionServiceProvider(config.getService().getTranscription()));
    }

    /**
     * @see AiServiceManager#getTranscriptionModelService(AiServiceProvider)
     */
    @Override
    public TranscriptionModelService getTranscriptionModelService(AiServiceProvider serviceProvider) {
        return this.transcriptionModelServices.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable TranscriptionModelService."));
    }

    /**
     * @see AiServiceManager#getTranscriptionModelService(ModelConfig)
     */
    @Override
    public TranscriptionModelService getTranscriptionModelService(ModelConfig config) {
        if (StringUtils.isEmpty(config.getServiceProvider())) {
            throw new IllegalArgumentException("TranscriptionModelService - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(config.getModelProvider())) {
            throw new IllegalArgumentException("TranscriptionModelService - modelProvider cannot be null");
        }
        if (config.getName() == null) {
            throw new IllegalArgumentException("TranscriptionModelService - modelName cannot be null");
        }

        TranscriptionModelService service = getTranscriptionModelService(AiServiceProvider.getTranscriptionServiceProvider(config.getServiceProvider()));
        if (service.supports(config)) {
            return service;
        }
        return getTranscriptionModelService();
    }

    // ------------------------------------------------------------------------------
    // Rerank
    // ------------------------------------------------------------------------------

    /**
     * @see AiServiceManager#getRerankModelService()
     */
    @Override
    public RerankModelService getRerankModelService() {
        return getRerankModelService(AiServiceProvider.getRerankServiceProvider(config.getService().getRerank()));
    }

    /**
     * @see AiServiceManager#getRerankModelService(AiServiceProvider)
     */
    @Override
    public RerankModelService getRerankModelService(AiServiceProvider serviceProvider) {
        return this.rerankModelServices.stream()
            .filter(service -> service.getServiceProvider().equals(serviceProvider))
            .filter(service -> service.getServiceProvider().isEnabled())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unavailable RerankModelService."));
    }

    /**
     * @see AiServiceManager#getRerankModelService(ModelConfig)
     */
    @Override
    public RerankModelService getRerankModelService(ModelConfig modelConfig) {
        if (StringUtils.isEmpty(modelConfig.getServiceProvider())) {
            throw new IllegalArgumentException("RerankModelService - serviceProvider cannot be null");
        }
        if (StringUtils.isEmpty(modelConfig.getModelProvider())) {
            throw new IllegalArgumentException("RerankModelService - modelProvider cannot be null");
        }
        if (modelConfig.getName() == null) {
            throw new IllegalArgumentException("RerankModelService - modelName cannot be null");
        }

        RerankModelService service = getRerankModelService(
            AiServiceProvider.getRerankServiceProvider(modelConfig.getServiceProvider()));
        if (service.supports(modelConfig)) {
            return service;
        }
        return this.getRerankModelService();
    }

}
