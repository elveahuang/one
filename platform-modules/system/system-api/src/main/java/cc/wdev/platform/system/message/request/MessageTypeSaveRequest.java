package cc.wdev.platform.system.message.request;

import cc.wdev.platform.commons.web.request.Request;
import cc.wdev.platform.system.message.domain.vo.MessageTemplateVo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息类型请求参数")
public class MessageTypeSaveRequest extends Request {
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
    @NotBlank(message = "标题不能为空")
    private String title;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 发布状态
     */
    @Schema(description = "发布状态")
    private Integer status;
    /**
     * 消息模版
     */
    @Schema(description = "消息模版")
    private List<MessageTemplateVo> items;
}
