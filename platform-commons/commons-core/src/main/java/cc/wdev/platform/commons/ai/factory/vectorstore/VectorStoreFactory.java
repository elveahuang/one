package cc.wdev.platform.commons.ai.factory.vectorstore;

import cc.wdev.platform.commons.ai.enums.AiVectorStoreType;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;

/**
 * @author elvea
 */
public interface VectorStoreFactory {

    AiVectorStoreType getStoreType();

    VectorStore getVectorStore(EmbeddingModel embeddingModel);

    VectorStore getVectorStore(EmbeddingModel embeddingModel, String collectionName);

}
