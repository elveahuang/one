package cc.wdev.platform.system.message.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息类型模版")
public class MessageTemplateVo implements Serializable {
    /**
     * ID
     */
    @Schema(description = "主键ID")
    private Long id;
    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String messageType;
    /**
     * 消息通道
     */
    @Schema(description = "消息通道")
    private String messageChannel;
    /**
     * 模版内容
     */
    @Schema(description = "模版内容")
    private String content;
    /**
     * 发布状态
     */
    @Schema(description = "发布状态")
    private Integer status;
}
