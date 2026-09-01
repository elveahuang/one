package cc.wdev.platform.commons.ai.utils;

import cc.wdev.platform.commons.ai.domain.response.SimpleTranscriptionResponse;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionApiKeywords;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author elvea
 */
public abstract class AiDashScopeUtils {

    public static SimpleTranscriptionResponse.Response getTranscriptionResponse(JsonObject jsonObject) {
        SimpleTranscriptionResponse.Response content = new SimpleTranscriptionResponse.Response();
        if (jsonObject.has(TranscriptionApiKeywords.FILE_URL)) {
            content.setFileUrl(jsonObject.get(TranscriptionApiKeywords.FILE_URL).getAsString());
        }
        if (jsonObject.has("transcripts")) {
            JsonArray array = jsonObject.get("transcripts").getAsJsonArray();

            for (JsonElement object : array) {
                JsonObject transcript = object.getAsJsonObject();
                if (transcript.has("text")) {
                    content.setText(transcript.get("text").getAsString());
                }
                if (transcript.has("sentences")) {
                    content.setSentences(getTranscriptionSentences(transcript));
                }
            }
        }
        return content;
    }

    public static List<SimpleTranscriptionResponse.Sentence> getTranscriptionSentences(JsonObject jsonObject) {
        List<SimpleTranscriptionResponse.Sentence> sentences = new ArrayList<>();
        if (jsonObject.has("sentences")) {
            JsonArray array = jsonObject.get("sentences").getAsJsonArray();
            for (JsonElement object : array) {
                SimpleTranscriptionResponse.Sentence sentence = new SimpleTranscriptionResponse.Sentence();

                JsonObject transcript = object.getAsJsonObject();
                if (transcript.has("sentence_id")) {
                    sentence.setId(transcript.get("sentence_id").getAsLong());
                }
                if (transcript.has("begin_time")) {
                    sentence.setBeginTime(transcript.get("begin_time").getAsLong());
                }
                if (transcript.has("end_time")) {
                    sentence.setEndTime(transcript.get("end_time").getAsLong());
                }
                if (transcript.has("text")) {
                    sentence.setText(transcript.get("text").getAsString());
                }

                sentences.add(sentence);
            }
        }
        return sentences;
    }

}
