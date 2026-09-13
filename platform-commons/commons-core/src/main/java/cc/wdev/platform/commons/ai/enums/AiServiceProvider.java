package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.utils.StringUtils;
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
    SPRING_AI_ANTHROPIC("SPRING_AI_ANTHROPIC", "Spring AI Anthropic", true),
    SPRING_AI_DASHSCOPE("SPRING_AI_DASHSCOPE", "Agentic Spring AI DashScope", true),
    ALIYUN_DASHSCOPE_SDK("ALIYUN_DASHSCOPE_SDK", "Aliyun DashScope SDK", true),
    TENCENT_HUNYUAN_SDK("TENCENT_HUNYUAN_SDK", "Tencent HunYuan SDK", true),
    OPENAI_SDK("OPENAI_SDK", "OpenAI SDK", true),
    CUSTOM("CUSTOM", "Custom Service", true);

    private final String value;
    private final String description;
    private final boolean enabled;

    /**
     * 自适应解析服务提供商
     * 支持大小写不敏感、中划线与下划线互转、以及常用简写别名
     */
    public static AiServiceProvider resolve(String provider) {
        if (StringUtils.isEmpty(provider)) {
            return null;
        }
        String normalized = provider.trim().toUpperCase().replace("-", "_");
        for (AiServiceProvider p : values()) {
            if (p.name().equals(normalized) || p.getValue().equals(normalized)) {
                return p;
            }
        }
        if ("DEEPSEEK".equals(normalized)) {
            return SPRING_AI_DEEPSEEK;
        }
        if ("OPENAI".equals(normalized)) {
            return SPRING_AI_OPENAI;
        }
        if ("ANTHROPIC".equals(normalized) || "CLAUDE".equals(normalized)) {
            return SPRING_AI_ANTHROPIC;
        }
        if ("DASHSCOPE".equals(normalized) || "ALIYUN".equals(normalized)) {
            return SPRING_AI_DASHSCOPE;
        }
        if ("ALIYUN_SDK".equals(normalized) || "DASHSCOPE_SDK".equals(normalized)) {
            return ALIYUN_DASHSCOPE_SDK;
        }
        if ("TENCENT".equals(normalized) || "HUNYUAN".equals(normalized) || "TENCENT_SDK".equals(normalized)) {
            return TENCENT_HUNYUAN_SDK;
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Model Service
    // ------------------------------------------------------------------------

    public static AiServiceProvider getChatServiceProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        if (resolved == SPRING_AI_OPENAI || resolved == OPENAI_SDK) {
            return OPENAI_SDK;
        }
        return resolved != null ? resolved : OPENAI_SDK;
    }

    public static AiServiceProvider getEmbeddingServiceProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        if (resolved == SPRING_AI_DASHSCOPE || resolved == ALIYUN_DASHSCOPE_SDK) {
            return ALIYUN_DASHSCOPE_SDK;
        }
        return resolved != null ? resolved : ALIYUN_DASHSCOPE_SDK;
    }

    public static AiServiceProvider getImageServiceProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        if (resolved == SPRING_AI_DASHSCOPE || resolved == ALIYUN_DASHSCOPE_SDK) {
            return ALIYUN_DASHSCOPE_SDK;
        }
        if (resolved == TENCENT_HUNYUAN_SDK) {
            return TENCENT_HUNYUAN_SDK;
        }
        return resolved != null ? resolved : ALIYUN_DASHSCOPE_SDK;
    }

    public static AiServiceProvider getSpeechServiceProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        if (resolved == SPRING_AI_DASHSCOPE || resolved == ALIYUN_DASHSCOPE_SDK) {
            return ALIYUN_DASHSCOPE_SDK;
        }
        return resolved != null ? resolved : ALIYUN_DASHSCOPE_SDK;
    }

    public static AiServiceProvider getTranscriptionServiceProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        if (resolved == SPRING_AI_DASHSCOPE || resolved == ALIYUN_DASHSCOPE_SDK) {
            return ALIYUN_DASHSCOPE_SDK;
        }
        if (resolved == TENCENT_HUNYUAN_SDK) {
            return TENCENT_HUNYUAN_SDK;
        }
        return resolved != null ? resolved : ALIYUN_DASHSCOPE_SDK;
    }

    public static AiServiceProvider getRerankServiceProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        if (resolved == SPRING_AI_DASHSCOPE || resolved == ALIYUN_DASHSCOPE_SDK) {
            return ALIYUN_DASHSCOPE_SDK;
        }
        return resolved != null ? resolved : ALIYUN_DASHSCOPE_SDK;
    }

    // ------------------------------------------------------------------------
    // Model Factory
    // ------------------------------------------------------------------------

    public static AiServiceProvider getChatFactoryProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        return resolved != null ? resolved : SPRING_AI_DEEPSEEK;
    }

    public static AiServiceProvider getEmbeddingFactoryProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        return resolved != null ? resolved : SPRING_AI_OPENAI;
    }

    public static AiServiceProvider getImageFactoryProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        return resolved != null ? resolved : SPRING_AI_OPENAI;
    }

    public static AiServiceProvider getSpeechFactoryProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        return resolved != null ? resolved : SPRING_AI_OPENAI;
    }

    public static AiServiceProvider getTranscriptionFactoryProvider(String provider) {
        AiServiceProvider resolved = resolve(provider);
        return resolved != null ? resolved : SPRING_AI_OPENAI;
    }

}
