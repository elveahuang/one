package cc.wdev.platform.system.tag.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

import static java.util.Collections.emptyList;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "标签类型", name = "标签类型")
public class TagTypeVo implements Serializable {
    /**
     * 标签类型编号
     */
    @Schema(title = "标签类型编号", description = "标签类型编号")
    private String code;
    /**
     * 标签明细
     */
    @Builder.Default
    @Schema(title = "标签明细", description = "标签明细")
    private Collection<TagVo> items = emptyList();
}
