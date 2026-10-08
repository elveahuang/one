package cc.wdev.platform.system.log.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchLogSaveRequest extends Request {

    @Schema(title = "搜索关键词", description = "搜索关键词")
    @NotEmpty(message = "搜索关键词不能为空")
    @Size(max = 120, message = "搜索关键词不能超过120个字符")
    private String searchKey;
}
