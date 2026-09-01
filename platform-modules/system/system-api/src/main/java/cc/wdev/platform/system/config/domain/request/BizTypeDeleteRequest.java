package cc.wdev.platform.system.config.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BizTypeDeleteRequest extends Request {
    @Schema(description = "业务分组类型")
    private String bizGroupType;
    @Schema(description = "业务类型")
    private String bizType;
    @Schema(description = "业务类型列表")
    private List<String> bizTypeList;
}
