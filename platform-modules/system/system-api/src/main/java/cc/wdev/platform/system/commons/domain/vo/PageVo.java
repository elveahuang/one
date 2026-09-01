package cc.wdev.platform.system.commons.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@Schema(description = "页面信息")
public class PageVo implements Serializable {

    @Schema(description = "页面标题")
    private String title;

    @Schema(description = "页面内容")
    private String content;

}
