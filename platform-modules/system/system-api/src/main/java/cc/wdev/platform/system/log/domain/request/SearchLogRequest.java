package cc.wdev.platform.system.log.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchLogRequest implements Serializable {

    @Schema(description = "搜索关键词")
    @NotEmpty(message = "搜索关键词不能为空")
    private String searchKey;
}
