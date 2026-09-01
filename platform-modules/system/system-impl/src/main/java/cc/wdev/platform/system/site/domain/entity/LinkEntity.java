package cc.wdev.platform.system.site.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_link")
@Schema(description = "友情链接实体")
public class LinkEntity extends BaseEntity {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "链接")
    private String link;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "索引")
    private Integer idx;

    @Schema(description = "友情链接关联字典")
    @TableField(exist = false)
    private RelationVo<?> type;

    @Schema(description = "友情链接分类关联字典")
    @TableField(exist = false)
    private RelationVo<?> linkType;

    @Schema(description = "封面")
    @TableField(exist = false)
    private AttachmentVo cover;

    @Schema(description = "摘要")
    private String summary;

}
