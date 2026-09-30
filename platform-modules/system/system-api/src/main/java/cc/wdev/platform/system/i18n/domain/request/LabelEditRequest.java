package cc.wdev.platform.system.i18n.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelEditRequest implements Serializable {
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    private Long id;
    /**
     * 分组
     */
    @Schema(title = "分组", description = "分组")
    private String labelGroupType;
    /**
     * 标识
     */
    @Schema(title = "标识", description = "标识")
    private String code;
    /**
     * 简体中文
     */
    @Schema(title = "简体中文", description = "简体中文")
    private String zhCnLabel;
    @Schema(title = "是否静态文本", description = "是否静态文本")
    private Integer zhCnStaticInd;
    /**
     * 繁体中文
     */
    @Schema(title = "繁体中文", description = "繁体中文")
    private String zhTwLabel;
    @Schema(title = "是否静态文本", description = "是否静态文本")
    private Integer zhTwStaticInd;
    /**
     * 英语
     */
    @Schema(title = "英语", description = "英语")
    private String enLabel;
    @Schema(title = "是否静态文本", description = "是否静态文本")
    private Integer enStaticInd;
    /**
     * 法语
     */
    @Schema(title = "法语", description = "法语")
    private String frLabel;
    @Schema(title = "是否静态文本", description = "是否静态文本")
    private Integer frStaticInd;
    /**
     * 日语
     */
    @Schema(title = "日语", description = "日语")
    private String jaLabel;
    @Schema(title = "是否静态文本", description = "是否静态文本")
    private Integer jaStaticInd;
    /**
     * 韩语
     */
    @Schema(title = "韩语", description = "韩语")
    private String krLabel;
    @Schema(title = "是否静态文本", description = "是否静态文本")
    private Integer krStaticInd;
    /**
     * 越南语
     */
    @Schema(description = "越南语")
    private String viLabel;
    @Schema(description = "越南语静态标志")
    private Integer viStaticInd;
}
