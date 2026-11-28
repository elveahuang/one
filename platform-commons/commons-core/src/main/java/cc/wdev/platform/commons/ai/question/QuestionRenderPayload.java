package cc.wdev.platform.commons.ai.question;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springaicommunity.agent.tools.AskUserQuestionTool;

import java.util.List;

/**
 * Question 交互卡 json-render 协议
 * <p>
 * 对应前端 {@code {"type":"Question","props":{...}}} 结构，避免在服务实现里逐字段拼 Map。
 * Gson 默认不序列化 null 字段，因此 conversationId / userId 为空时会自动省略。
 *
 * @author dev
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRenderPayload {

    @Builder.Default
    private String type = "Question";

    private Props props;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Props {

        private String conversationId;

        private String userId;

        @SerializedName("QuestionData")
        private List<AskUserQuestionTool.Question> questionData;
    }
}
