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
    SPRING_AI_DEEPSEEK("SPRING_AI_DEEPSEEK", "Spring AI DeepSeek", true),
    SPRING_AI_OPENAI("SPRING_AI_OPENAI", "Spring AI OpenAI", true),
    SPRING_AI_DASHSCOPE("SPRING_AI_DASHSCOPE", "Agentic Spring AI DashScope", true),
    ALIYUN_DASHSCOPE_SDK("ALIYUN_DASHSCOPE_SDK", "Aliyun DashScope SDK", true),
    TENCENT_HUNYUAN_SDK("TENCENT_HUNYUAN_SDK", "Tencent HunYuan SDK", true),
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
