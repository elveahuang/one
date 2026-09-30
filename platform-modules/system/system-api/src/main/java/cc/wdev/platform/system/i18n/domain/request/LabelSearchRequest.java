package cc.wdev.platform.system.i18n.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabelSearchRequest extends PageRequest {
    /**
     * id
     */
    @Schema(title = "id", description = "id")
    private Long id;
    /**
     * ids
     */
    @Schema(title = "ids", description = "ids")
    private List<Long> ids;
    /**
     * 导入模板
     */
    @Schema(title = "导入模板", description = "是否导入模板")
    private Boolean isTemplate;
    /**
     * 标识
     */
    @Schema(title = "标识", description = "标识")
    private String code;
    private String oldCode;
}
