package cc.wdev.platform.system.tag.domain.request;

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
@Schema(description = "排序ID数组")
public class TagSortRequest extends Request {

    @Schema(description = "排序ID数组")
    private List<Long> ids;

    @Schema(description = "业务ID")
    private Long bizId;

    @Schema(description = "业务类型")
    private String bizType;
}
