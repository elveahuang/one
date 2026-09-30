package cc.wdev.platform.system.site.domain.form;

import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LinkForm implements Serializable {
    /**
     * ID
     */
    @Schema(title = "友情链接ID", description = "友情链接ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 标题
     */
    @NotEmpty
    @Schema(title = "标题", description = "标题")
    private String title;
    /**
     * 副标题
     */
    @NotEmpty
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
     * 字典类型
     */
    @Schema(title = "类型", description = "字典类型")
    private RelationVo<?> type;

    /**
     * 友情链接分类字典
     */
    @Schema(title = "分类", description = "分类")
    private RelationVo<?> linkType;


    /**
     * 封面
     */
    @Schema(title = "封面", description = "封面")
    private AttachmentVo cover;

}
