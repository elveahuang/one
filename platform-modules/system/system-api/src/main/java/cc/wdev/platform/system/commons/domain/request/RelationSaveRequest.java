package cc.wdev.platform.system.commons.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "关联保存参数")
public class RelationSaveRequest extends Request {
    /**
     * 关联业务类型
     */
    @Schema(description = "关联业务类型")
    private String relationBizType;
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
     * 关联ID
     */
    @Schema(description = "关联ID")
    private Long[] ids;
}
