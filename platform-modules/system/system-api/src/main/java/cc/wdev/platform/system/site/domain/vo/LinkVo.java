package cc.wdev.platform.system.site.domain.vo;

import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "友情链接對象")
public class LinkVo {
    /**
     * ID
     */
    @Schema(title = "ID", description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 标题
     */
    @Schema(title = "标题", description = "标题")
    private String title;
    /**
     * 副标题
     */
    @Schema(title = "副标题", description = "副标题")
    private String subTitle;
    /**
     * 链接
     */
    @Schema(title = "链接", description = "链接")
    private String link;
    /**
     * 描述
     */
    @Schema(title = "描述", description = "描述")
    private String description;
    /**
     * 简介
     */
    @Schema(title = "简介", description = "简介")
    private String summary;
    /**
     * 序号
     */
    @Schema(title = "序号", description = "序号")
    private Integer idx;
    /**
     * 类型
     */
    @Schema(title = "类型", description = "类型")
    private RelationVo<?> type;

    /**
     * 分类
     */
    @Schema(title = "分类", description = "分类")
    private RelationVo<?> linkType;

    /**
     * 封面
     */
    @Schema(title = "封面", description = "封面")
    private AttachmentVo cover;
}
