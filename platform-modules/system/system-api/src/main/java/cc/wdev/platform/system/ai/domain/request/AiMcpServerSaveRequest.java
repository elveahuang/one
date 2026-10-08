package cc.wdev.platform.system.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "AiMcpServer表单", description = "AiMcpServer表单")
public class AiMcpServerSaveRequest implements Serializable {
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 编号
     */
    @Schema(title = "编号", description = "编号")
    private String code;

    /**
     * 标题
     */
    @Schema(title = "标题", description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 协议
     */
    @Schema(title = "协议", description = "协议")
    @NotBlank(message = "协议不能为空")
    private String protocol;

    /**
     * 服务地址
     */
    @Schema(title = "服务地址", description = "服务地址")
    @NotBlank(message = "服务地址不能为空")
    private String url;

    /**
     * 环境变量
     */
    @Schema(title = "环境变量", description = "环境变量")
    private String headers;

    /**
     * 参数
     */
    @Schema(title = "参数", description = "参数")
    private String args;

    /**
     * 备注说明
     */
    @Schema(title = "备注说明", description = "备注说明")
    private String description;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;

}
