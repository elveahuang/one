package cc.wdev.platform.system.catalog.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Schema(description = "分类保存请求")
public class CatalogSaveRequest implements Serializable {
    private Long id;
    private Long parentId;
    @Schema(description = "业务类型")
    @Size(max = 100, message = "业务类型长度不能超过100个字符")
    private String bizType;
    private Long bizId;
    @Schema(description = "编号")
    @NotBlank(message = "分类编号不能为空")
    @Size(max = 100, message = "分类编号长度不能超过100个字符")
    private String code;
    @Schema(description = "标题")
    @NotBlank(message = "分类标题不能为空")
    @Size(max = 150, message = "分类标题长度不能超过150个字符")
    private String title;
    @Schema(description = "描述")
    @Size(max = 255, message = "描述长度不能超过255个字符")
    private String description;
}
