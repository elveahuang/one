package cc.wdev.platform.system.catalog.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "分类删除请求")
public class CatalogDeleteRequest implements Serializable {
    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long id;
}
