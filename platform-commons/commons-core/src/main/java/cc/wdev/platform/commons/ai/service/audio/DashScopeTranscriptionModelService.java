package cc.wdev.platform.commons.ai.service.audio;

import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.config.ModelTranscriptionConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleTranscriptionRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleTranscriptionResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import cc.wdev.platform.commons.ai.utils.AiDashScopeUtils;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import com.alibaba.dashscope.audio.asr.transcription.*;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildTranscriptionModelConfig;

/**
 * @author elvea
 */
@Slf4j
public class DashScopeTranscriptionModelService extends AbstractTranscriptionModelService {

    public DashScopeTranscriptionModelService(ModelCommonsConfig commonsConfig, ModelTranscriptionConfig modelConfig) {
        super(buildTranscriptionModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see TranscriptionModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.ALIYUN_DASHSCOPE_SDK;
    }

    /**
     * @see TranscriptionModelService#call(SimpleTranscriptionRequest)
     */
    @Override
    public SimpleTranscriptionResponse<TranscriptionResult, TranscriptionResult> call(SimpleTranscriptionRequest request) {
        TranscriptionParam param = TranscriptionParam.builder()
            .apiKey(this.config.getApiKey())
            .model(this.config.getName())
            .parameter("language_hints", new String[]{"zh", "en"})
            .fileUrls(List.of(request.getFileUrl()))
            .build();

        SimpleTranscriptionResponse<TranscriptionResult, TranscriptionResult> response = new SimpleTranscriptionResponse<>();
        try {
            Transcription transcription = new Transcription();

            // 提交任务，获取请求任务信息
            TranscriptionResult postResult = transcription.asyncCall(param);
            log.info("postResult: {}", GsonUtils.toJson(postResult));

            response.setPostResult(postResult);
            response.setRequestId(postResult.getRequestId());
            response.setTaskId(postResult.getTaskId());

            // 阻塞等待任务完成并获取结果
            TranscriptionResult fetchResult = transcription.wait(TranscriptionQueryParam.FromTranscriptionParam(param, postResult.getTaskId()));
            log.info("fetchResult: {}", GsonUtils.toJson(fetchResult));

            response.setTaskResult(fetchResult);

            // 处理任务结果
            List<TranscriptionTaskResult> results = fetchResult.getResults();
            if (CollectionUtils.isNotEmpty(results)) {
                JsonObject jsonObject = GsonUtils.fromUrl(results.getFirst().getTranscriptionUrl());
                SimpleTranscriptionResponse.Response content = AiDashScopeUtils.getTranscriptionResponse(jsonObject);
                response.setResponse(content);
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return response;
    }

}
