package cc.wdev.platform.system.core.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "编辑套餐请求")
public class EditTenantPackageForm extends AddTenantPackageForm {

    /**
     * ID
     */
    @Schema(description = "套餐ID")
    @NotNull(message = "套餐ID不能为空")
    protected Long id;
}
