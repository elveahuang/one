package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务提供商
 * 底层的技术实现方案
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiServiceProvider implements BaseEnum<String> {
    SPRING_AI_DEEPSEEK("SPRING_AI_DEEPSEEK", "深度求索 - Spring AI DeekSeek", true),
    SPRING_AI_OPENAI("SPRING_AI_OPENAI", "OpenAI - Spring AI OpenAI", true),
    AGENTIC_SPRING_AI("AGENTIC_SPRING_AI", "Agentic Spring AI", true),
    ALIYUN_DASHSCOPE_SDK("ALIYUN_DASHSCOPE_SDK", "阿里云 - DashScope SDK", true),
    TENCENT_HUNYUAN_SDK("TENCENT_HUNYUAN_SDK", "腾讯云 - HunYuan SDK", false),
    OPENAI_SDK("OPENAI_SDK", "OpenAI SDK", true),
    CUSTOM("CUSTOM", "Custom Service", true);

    private final String value;
    private final String description;
    private final boolean enabled;

    // ------------------------------------------------------------------------
    // Model Service
    // ------------------------------------------------------------------------

    public static AiServiceProvider getChatServiceProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.OPENAI_SDK);
    }

    public static AiServiceProvider getEmbeddingServiceProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
    }

    public static AiServiceProvider getImageServiceProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
    }

    public static AiServiceProvider getSpeechServiceProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
    }

    public static AiServiceProvider getTranscriptionServiceProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
    }

    public static AiServiceProvider getRerankServiceProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.ALIYUN_DASHSCOPE_SDK);
    }

    // ------------------------------------------------------------------------
    // Model Factory
    // ------------------------------------------------------------------------

    public static AiServiceProvider getChatFactoryProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.SPRING_AI_DEEPSEEK);
    }

    public static AiServiceProvider getEmbeddingFactoryProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.SPRING_AI_OPENAI);
    }

    public static AiServiceProvider getImageFactoryProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.SPRING_AI_OPENAI);
    }

    public static AiServiceProvider getSpeechFactoryProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.SPRING_AI_OPENAI);
    }

    public static AiServiceProvider getTranscriptionFactoryProvider(String provider) {
        return BaseEnum.getEnumByValue(provider, AiServiceProvider.class, AiServiceProvider.SPRING_AI_OPENAI);
    }

}
