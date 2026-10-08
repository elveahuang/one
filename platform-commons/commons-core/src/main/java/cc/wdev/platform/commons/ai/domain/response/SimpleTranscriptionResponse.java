package cc.wdev.platform.commons.ai.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleTranscriptionResponse<R, TR> implements Serializable {

    /**
     * Request ID
     */
    private String requestId;

    /**
     * Task ID
     */
    private String taskId;

    /**
     * 请求结果
     */
    private R postResult;

    /**
     * 任务结果
     */
    private TR taskResult;

    /**
     * 最终响应文本
     */
    private String responseType;

    /**
     * 最终结果
     */
    private Response response;

    @Data
    public static class Response {
        private String fileUrl;
        private String text;
        private List<Sentence> sentences = new ArrayList<>();
    }

    @Data
    public static class Sentence {
        private Long id;
        private Long beginTime;
        private Long endTime;
        private String text;
    }

}
