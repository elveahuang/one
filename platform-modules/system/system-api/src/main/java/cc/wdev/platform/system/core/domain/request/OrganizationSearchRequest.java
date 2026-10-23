package cc.wdev.platform.system.core.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
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
@Schema(description = "部门查询请求")
public class OrganizationSearchRequest extends PageRequest {

    @Schema(description = "父级部门ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

}
