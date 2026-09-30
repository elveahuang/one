package cc.wdev.platform.commons.ai.factory.embedding;

import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import org.springframework.ai.embedding.EmbeddingModel;

/**
 * @author elvea
 */
public interface EmbeddingModelFactory extends ModelFactory<EmbeddingModel> {

    /**
     * @see ModelFactory#getModelType()
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.EMBEDDING;
    }

    /**
     * 获取向量模型
     */
    default EmbeddingModel getEmbeddingModel() {
        return this.getEmbeddingModel(this.getModelConfig());
    }

    /**
     * 获取向量模型
     */
    default EmbeddingModel getEmbeddingModel(ModelConfig config) {
        return this.getModel(config);
    }

}
