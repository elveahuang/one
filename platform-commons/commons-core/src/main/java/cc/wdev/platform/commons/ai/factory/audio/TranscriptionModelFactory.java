package cc.wdev.platform.commons.ai.factory.audio;

import cc.wdev.platform.commons.ai.enums.AiModelType;
import cc.wdev.platform.commons.ai.factory.ModelFactory;
import cc.wdev.platform.commons.ai.model.ModelConfig;
import org.springframework.ai.audio.transcription.TranscriptionModel;

/**
 * @author elvea
 */
public interface TranscriptionModelFactory extends ModelFactory<TranscriptionModel> {

    /**
     * @see ModelFactory#getModelType()
     */
    @Override
    default AiModelType getModelType() {
        return AiModelType.AUDIO_TRANSCRIPTION;
    }

    /**
     * 获取转录模型
     */
    default TranscriptionModel getTranscriptionModel() {
        return this.getTranscriptionModel(this.getModelConfig());
    }

    /**
     * 获取转录模型
     */
    default TranscriptionModel getTranscriptionModel(ModelConfig config) {
        return this.getModel(config);
    }

}
