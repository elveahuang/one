package cc.wdev.platform.system.ai.enums;

import cc.wdev.platform.commons.ai.enums.AiModelProvider;
import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiModelBizTypeEnum implements BaseAiModelBizTypeEnum {
    // DeepSeek
    DEEPSEEK_V4_FLASH(AiServiceProvider.SPRING_AI_DEEPSEEK.getValue(), AiModelProvider.DEEPSEEK.getValue(), AiModelType.TEXT.getValue(), "deepseek-v4-flash", "deepseek-v4-flash"),
    DEEPSEEK_V4_PRO(AiServiceProvider.SPRING_AI_DEEPSEEK.getValue(), AiModelProvider.DEEPSEEK.getValue(), AiModelType.TEXT.getValue(), "deepseek-v4-pro", "deepseek-v4-pro"),
    // Aliyun
    ALIYUN_DEEPSEEK_FLASH(AiServiceProvider.SPRING_AI_OPENAI.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.TEXT.getValue(), "deepseek-v4-flash", "deepseek-v4-flash"),
    ALIYUN_DEEPSEEK_PRO(AiServiceProvider.SPRING_AI_OPENAI.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.TEXT.getValue(), "deepseek-v4-pro", "deepseek-v4-pro"),
    ALIYUN_QWEN_PLUS(AiServiceProvider.SPRING_AI_OPENAI.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.TEXT.getValue(), "qwen-plus", "qwen-plus"),
    ALIYUN_QWEN_TEXT_EMBEDDING(AiServiceProvider.SPRING_AI_OPENAI.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.EMBEDDING.getValue(), "qwen3.7-text-embedding", "qwen3.7-text-embedding"),
    ALIYUN_TEXT_EMBEDDING(AiServiceProvider.SPRING_AI_OPENAI.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.EMBEDDING.getValue(), "text-embedding-v4", "text-embedding-v4"),
    ALIYUN_QWEN_IMAGE_PLUS(AiServiceProvider.ALIYUN_DASHSCOPE_SDK.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.IMAGE.getValue(), "qwen-image-plus", "qwen-image-plus"),
    ALIYUN_QWEN_ASR_FLASH(AiServiceProvider.ALIYUN_DASHSCOPE_SDK.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.AUDIO_TRANSCRIPTION.getValue(), "qwen3-asr-flash", "qwen3-asr-flash"),
    ALIYUN_PARAFORMER_V2(AiServiceProvider.ALIYUN_DASHSCOPE_SDK.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.AUDIO_TRANSCRIPTION.getValue(), "paraformer-v2", "paraformer-v2"),
    // Rerank
    ALIYUN_GTE_RERANK(AiServiceProvider.ALIYUN_DASHSCOPE_SDK.getValue(), AiModelProvider.ALIYUN.getValue(), AiModelType.RERANK.getValue(), "gte-rerank-v3", "gte-rerank-v3"),
    ;

    private final String serviceProvider;
    private final String modelProvider;
    private final String modelType;
    private final String modelName;
    private final String description;
}
