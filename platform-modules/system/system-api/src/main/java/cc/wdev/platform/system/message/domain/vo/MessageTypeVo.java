package cc.wdev.platform.system.message.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息类型", name = "消息类型")
public class MessageTypeVo implements Serializable {
    /**
     * ID
     */
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String label;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;
    /**
     * 发布状态
     */
    @Schema(description = "发布状态")
    private Integer status;
    /**
     * 消息模版
     */
    @Builder.Default
    @Schema(title = "消息模版", description = "消息模版")
    private List<MessageTemplateVo> items = emptyList();
}
