package cc.wdev.platform.system.core.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "套餐关联权限请求")
public class EditTenantPackageAuthorityForm {
    @Schema(description = "套餐ID")
    @NotNull(message = "套餐ID不能为空")
    private Long packageId;

    @Schema(description = "权限ID列表")
    @NotEmpty(message = "请选择至少一个权限")
    private List<Long> authorityIds;
}
