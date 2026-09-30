package cc.wdev.platform.commons.ai.domain.chat;

import cc.wdev.platform.commons.ai.ui.UiBlock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 对话响应内容
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "对话响应内容")
public class SimpleChatContent implements Serializable {
    /**
     * 类型
     */
    @Schema(name = "类型", description = "类型")
    private String type;
    /**
     * 内容
     */
    @Schema(name = "内容", description = "内容")
    private String content;
    /**
     * 组件
     */
    @Schema(name = "组件", description = "组件")
    private UiBlock block;
}
