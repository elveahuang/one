package cc.wdev.platform.system.dict.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Schema(description = "字典类型", name = "字典类型")
public class DictTypeVo implements Serializable {
    /**
     * 字典类型编号
     */
    @Schema(title = "字典类型编号", description = "字典类型编号")
    private String code;
    /**
     * 字典明细
     */
    @Builder.Default
    @Schema(title = "字典明细", description = "字典明细")
    private List<DictVo> items = emptyList();
}
