package cc.wdev.platform.commons.ai.factory.audio;

import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import org.springframework.ai.audio.tts.TextToSpeechModel;

/**
 * @author elvea
 */
public interface SpeechModelFactory extends ModelFactory<TextToSpeechModel> {

    /**
     * @see ModelFactory#getModelType()
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.AUDIO_SPEECH;
    }

    /**
     * 获取转录模型
     */
    default TextToSpeechModel getSpeechModel() {
        return this.getSpeechModel(this.getModelConfig());
    }

    /**
     * 获取转录模型
     */
    default TextToSpeechModel getSpeechModel(ModelConfig config) {
        return this.getModel(config);
    }

}
