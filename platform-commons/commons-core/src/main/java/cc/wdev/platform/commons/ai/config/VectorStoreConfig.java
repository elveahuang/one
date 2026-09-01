package cc.wdev.platform.commons.ai.config;

import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.enums.AiVectorStoreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorStoreConfig implements Serializable {

    @Builder.Default
    private String type = AiVectorStoreType.ELASTICSEARCH.getValue();

    @Builder.Default
    private String embeddingProvider = AiServiceProvider.SPRING_AI_OPENAI.getValue();

    @Builder.Default
    private String indexPrefix = "platform-rag";

}
