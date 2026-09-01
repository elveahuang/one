package cc.wdev.platform.commons.ai.factory;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author elvea
 */
public class EmbeddingModelFactoryTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiManager);
    }

    @Test
    public void openaiEmbeddingTests() {
        EmbeddingModel embeddingModel = this.aiManager.getEmbeddingModelFactory(AiServiceProvider.SPRING_AI_OPENAI).getEmbeddingModel();
        Assertions.assertNotNull(embeddingModel);

        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(
            "Hello World",
            "World is big and salvation is near"
        ));
        Assertions.assertNotNull(response);
    }

}
