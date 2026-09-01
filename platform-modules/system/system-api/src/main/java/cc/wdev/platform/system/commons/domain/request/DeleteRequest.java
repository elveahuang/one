package cc.wdev.platform.system.commons.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeleteRequest extends Request {

    /**
     * 待删除实体ID列表
     */
    @Schema(description = "待删除实体ID列表")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] ids;

    /**
     * 是否强制删除
     */
    @Builder.Default
    @Schema(description = "强制删除，删除不可恢复")
    boolean force = false;

}
