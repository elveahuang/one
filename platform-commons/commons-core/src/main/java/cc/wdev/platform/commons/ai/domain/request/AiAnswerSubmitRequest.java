package cc.wdev.platform.commons.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 提交用户提问回答请求
 *
 * @author dfox
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "提交用户提问回答请求")
public class AiAnswerSubmitRequest implements Serializable {
    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private String conversationId;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 用户回答列表 (key: 问题描述, value: 选项值或自定义文本)
     */
    @NotEmpty(message = "回答内容不能为空")
    @Schema(description = "用户回答列表")
    private Map<String, String> answers;
}
