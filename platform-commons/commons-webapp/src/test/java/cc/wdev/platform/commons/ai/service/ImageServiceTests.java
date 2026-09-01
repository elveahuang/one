package cc.wdev.platform.commons.ai.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleImageRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleImageResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.image.ImageModelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class ImageServiceTests extends BaseTests {

    @Autowired
    private AiServiceManager aiServiceManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiServiceManager);
    }

    @Test
    public void dashScopeTest() {
        ImageModelService service = this.aiServiceManager.getImageService(AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
        Assertions.assertNotNull(service);

        SimpleImageRequest request = SimpleImageRequest.builder()
            .prompt("生成一个生日快乐的贺卡")
            .build();
        SimpleImageResponse<?> response = service.call(request);
        Assertions.assertNotNull(response);
    }

}
