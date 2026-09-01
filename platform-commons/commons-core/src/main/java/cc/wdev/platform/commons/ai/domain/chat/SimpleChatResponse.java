package cc.wdev.platform.commons.ai.domain.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

import java.io.Serializable;
import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "简单对话响应")
public class SimpleChatResponse implements Serializable {
    @Schema(description = "会话ID")
    private String conversationId;
    @Schema(description = "消息列表")
    private List<Message> messages;
}
