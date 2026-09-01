package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 模型提供商
 */
@Getter
@AllArgsConstructor
public enum AiModelProvider implements BaseEnum<String> {
    DEEPSEEK("deepseek", "深度求索", true,
        new Model[]{
            new Model("deepseek-v4-flash", true, false, AiModelType.TEXT),
            new Model("deepseek-v4-pro", true, false, AiModelType.TEXT)
        },
        new AiModelType[]{AiModelType.TEXT}
    ),

    ALIYUN("aliyun", "阿里云", true,
        new Model[]{
            new Model("deepseek-v4-flash", true, false, AiModelType.TEXT),
            new Model("deepseek-v4-pro", true, false, AiModelType.TEXT),
            new Model("qwen-plus", true, false, AiModelType.TEXT),
            new Model("qwen-image", true, false, AiModelType.IMAGE),
            new Model("qwen-image-plus", true, false, AiModelType.IMAGE),
            new Model("qwen-image-max", true, false, AiModelType.IMAGE),
            new Model("fun-asr", true, false, AiModelType.AUDIO_TRANSCRIPTION),
            new Model("fun-asr-realtime", true, false, AiModelType.AUDIO_TRANSCRIPTION),
            new Model("qwen3-asr-flash", true, false, AiModelType.AUDIO_TRANSCRIPTION),
            new Model("qwen3-asr-flash-filetrans", true, false, AiModelType.AUDIO_TRANSCRIPTION),
            new Model("qwen3-asr-flash-realtime", true, false, AiModelType.AUDIO_TRANSCRIPTION),
            new Model("paraformer-v2", true, false, AiModelType.AUDIO_TRANSCRIPTION),
            new Model("text-embedding-v4", false, false, AiModelType.EMBEDDING),
            new Model("gte-rerank", false, false, AiModelType.RERANK),
            new Model("text-rerank-v3", false, false, AiModelType.RERANK),
        },
        new AiModelType[]{AiModelType.TEXT, AiModelType.AUDIO_TRANSCRIPTION, AiModelType.EMBEDDING, AiModelType.RERANK}
    ),

    ORCAROUTER("ORCAROUTER", "OrcaRouter", true,
        new Model[]{
            new Model("orcarouter/free", true, false, AiModelType.TEXT),
            new Model("deepseek/deepseek-v4-flash-free", true, false, AiModelType.TEXT),
            new Model("deepseek/deepseek-v4-flash-0731", true, false, AiModelType.TEXT),
            new Model("deepseek/deepseek-v4-pro-0813", true, false, AiModelType.TEXT),
            new Model("deepseek/deepseek-v4-flash-vision-exp", true, false, AiModelType.TEXT)
        },
        new AiModelType[]{AiModelType.TEXT}
    ),

    OPENAI("openai", "OpenAI", true,
        new Model[]{},
        new AiModelType[]{AiModelType.TEXT, AiModelType.EMBEDDING}
    );

    private final String value;
    private final String description;
    private final boolean enabled;
    private final Model[] models;
    private final AiModelType[] types;

    public boolean supportsType(AiModelType type) {
        return type != null && Arrays.asList(types).contains(type);
    }

    public boolean supportsModel(String modelName) {
        return getModel(modelName).isPresent();
    }

    private Optional<Model> getModel(String modelName) {
        if (modelName == null) {
            return Optional.empty();
        }
        return Arrays.stream(models)
            .filter(cap -> modelName.equals(cap.modelName()) || modelName.startsWith(cap.modelName() + "-"))
            .findFirst();
    }

    /**
     * 模型定义
     *
     * @param modelName           模型名称
     * @param deepThinkingEnabled 是否支持深度思考
     * @param webSearchEnabled    是否支持网络搜索
     * @param type                模型类型
     */
    public record Model(String modelName, boolean deepThinkingEnabled, boolean webSearchEnabled, AiModelType type) {
    }

}
