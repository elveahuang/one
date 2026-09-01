package cc.wdev.platform.commons.ai.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleEmbeddingRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleEmbeddingResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.embedding.EmbeddingModelService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class EmbeddingServiceTests extends BaseTests {

    @Autowired
    private AiServiceManager aiServiceManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiServiceManager);
    }

    @Test
    public void dashScopeTest() {
        EmbeddingModelService service = this.aiServiceManager.getEmbeddingModelService(AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
        Assertions.assertNotNull(service);

        SimpleEmbeddingRequest request = SimpleEmbeddingRequest.builder()
            .texts(Lists.newArrayList("你好", "你好"))
            .build();
        SimpleEmbeddingResponse<?> response = service.call(request);
        Assertions.assertNotNull(response);
    }

}
