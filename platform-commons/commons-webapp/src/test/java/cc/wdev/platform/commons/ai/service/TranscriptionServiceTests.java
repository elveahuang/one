package cc.wdev.platform.commons.ai.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleTranscriptionRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleTranscriptionResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.service.audio.TranscriptionModelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class TranscriptionServiceTests extends BaseTests {

    private final static String url = "https://dashscope.oss-cn-beijing.aliyuncs.com/audios/welcome.mp3";

    @Autowired
    private AiServiceManager aiServiceManager;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiServiceManager);
    }

    @Test
    public void callTest() throws Exception {
        TranscriptionModelService service = this.aiServiceManager.getTranscriptionModelService(AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
        Assertions.assertNotNull(service);

        SimpleTranscriptionRequest request = SimpleTranscriptionRequest.builder().fileUrl(url).build();
        SimpleTranscriptionResponse<?, ?> response = service.call(request);
        Assertions.assertNotNull(response);
    }

    @Test
    public void asyncCallTest() throws Exception {
        TranscriptionModelService service = this.aiServiceManager.getTranscriptionModelService(AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
        Assertions.assertNotNull(service);

        SimpleTranscriptionRequest request = SimpleTranscriptionRequest.builder().fileUrl(url).build();
        SimpleTranscriptionResponse<?, ?> asyncCallResponse = service.call(request);
        Assertions.assertNotNull(asyncCallResponse);

        request.setTaskId(asyncCallResponse.getTaskId());
        SimpleTranscriptionResponse<?, ?> asyncResultResponse = service.call(request);
        Assertions.assertNotNull(asyncResultResponse);
    }

}
