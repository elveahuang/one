package cc.wdev.platform.commons.ai.sdk;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.AiServiceManager;
import cc.wdev.platform.commons.autoconfigure.ai.properties.AiAliyunProperties;
import cc.wdev.platform.commons.utils.GsonUtils;
import com.alibaba.dashscope.audio.asr.transcription.*;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.rerank.TextReRank;
import com.alibaba.dashscope.rerank.TextReRankParam;
import com.alibaba.dashscope.rerank.TextReRankResult;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static cc.wdev.platform.commons.ai.enums.AiServiceProvider.ALIYUN_DASHSCOPE_SDK;

/**
 * @author elvea
 */
@Slf4j
public class AliyunSdkTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private AiServiceManager aiServiceManager;

    @Autowired
    private AiAliyunProperties properties;

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
            .apiKey(this.aiServiceManager.getTranscriptionModelService(ALIYUN_DASHSCOPE_SDK).getModelConfig().getApiKey())
            .model(this.aiServiceManager.getTranscriptionModelService(ALIYUN_DASHSCOPE_SDK).getModelConfig().getName())
            .parameter("language_hints", new String[]{"zh", "en"})
            .fileUrls(Arrays.asList(
                "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav",
                "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_male2.wav"))
            .build();
        Transcription transcription = new Transcription();
        TranscriptionResult result = transcription.asyncCall(param);
        log.info(GsonUtils.toJson(result));
        result = transcription.wait(TranscriptionQueryParam.FromTranscriptionParam(param, result.getTaskId()));
        log.info(GsonUtils.toJson(result));
        List<TranscriptionTaskResult> taskResultList = result.getResults();
        Assertions.assertNotNull(taskResultList);
    }

    @Test
    public void reRankTest() throws NoApiKeyException, InputRequiredException {
        List<String> documents = Lists.newArrayList(
            "重排序模型广泛应用于搜索引擎和推荐系统，按相关性对候选文本进行排序",
            "量子计算是计算科学的前沿领域",
            "预训练语言模型的发展为重排序模型带来了新的进展"
        );
        TextReRankParam param = TextReRankParam.builder()
            .apiKey(this.aiServiceManager.getRerankModelService(ALIYUN_DASHSCOPE_SDK).getModelConfig().getApiKey())
            .model(this.aiServiceManager.getRerankModelService(ALIYUN_DASHSCOPE_SDK).getModelConfig().getName())
            .query("什么是重排序模型")
            .documents(documents)
            .topN(2)
            .returnDocuments(false)
            .build();
        TextReRank textReRank = new TextReRank();
        TextReRankResult result = textReRank.call(param);
        log.info(GsonUtils.toJson(result));
        Assertions.assertNotNull(result);
    }

}
