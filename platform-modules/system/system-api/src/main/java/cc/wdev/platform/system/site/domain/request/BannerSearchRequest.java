package cc.wdev.platform.system.site.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class BannerSearchRequest extends PageRequest {
    /**
     * 宣传栏类型关联字典项CODE
     */
    @Schema(title = "宣传栏类型关联字典项CODE", description = "宣传栏类型关联字典项CODE")
    private String[] itemCodes;
}
