package cc.wdev.platform.system.site.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class KeywordForm implements Serializable {

    /**
     * id
     */
    @Schema(description = "关键字ID")
    private Long id;

    /**
     * 关键字
     */
    @Schema(description = "关键字内容")
    private String content;

}
