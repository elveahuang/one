package cc.wdev.platform.system.config.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统页面")
public class AppPageForm implements Serializable {

    @Schema(description = "系统页面编号")
    private String code;

    @Schema(description = "系统页面内容")
    private String content;

}
