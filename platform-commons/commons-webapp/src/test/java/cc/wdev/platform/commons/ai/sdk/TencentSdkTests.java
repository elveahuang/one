package cc.wdev.platform.commons.ai.sdk;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiConfig;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiTencentProperties;
import com.alibaba.dashscope.audio.asr.transcription.*;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author elvea
 */
public class TencentSdkTests extends BaseTests {

    @Autowired
    private AiConfig aiConfig;

    @Autowired
    private AiManager aiManager;

    @Autowired
    private AiServiceManager aiServiceManager;

    @Autowired
    private AiTencentProperties properties;

    @Test
    public void baseTest() throws Exception {
        Assertions.assertNotNull(this.aiServiceManager);
    }

    @Test
    public void baseChatTest() {
        ChatClient chatClient = this.aiManager.getChatModelFactory().getChatClient();
        Assertions.assertNotNull(chatClient);
    }

    @Test
    public void transcriptionTest() {
        TranscriptionParam param = TranscriptionParam.builder()
            .apiKey(properties.getCommons().getApiKey())
            .model(properties.getTranscription().getName())
            .parameter("language_hints", new String[]{"zh", "en"})
            .fileUrls(Arrays.asList(
                "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav",
                "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_male2.wav"))
            .build();
        Transcription transcription = new Transcription();
        TranscriptionResult result = transcription.asyncCall(param);
        System.out.println("RequestId: " + result.getRequestId());
        result = transcription.wait(TranscriptionQueryParam.FromTranscriptionParam(param, result.getTaskId()));
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(result.getOutput()));
        List<TranscriptionTaskResult> taskResultList = result.getResults();
        Assertions.assertNotNull(taskResultList);
    }

}
