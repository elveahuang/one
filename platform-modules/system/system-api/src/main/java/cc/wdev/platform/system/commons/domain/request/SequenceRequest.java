package cc.wdev.platform.system.commons.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Map;

/**
 * 保存字典/标签个性化排序的请求对象
 *
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "保存个性化排序的请求对象")
public class SequenceRequest implements Serializable {
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 排序映射
     */
    @Schema(description = "排序映射")
    @NotEmpty(message = "排序映射不能为空")
    private Map<Long, Integer> sequence;
    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;
}
