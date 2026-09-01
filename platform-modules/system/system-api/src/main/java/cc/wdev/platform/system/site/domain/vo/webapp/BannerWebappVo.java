package cc.wdev.platform.system.site.domain.vo.webapp;

import cc.wdev.platform.system.storage.domain.vo.webapp.AttachmentVo;
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
@Schema(description = "宣传栏對象")
public class BannerWebappVo {
    /**
     * 主键
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
     * 序号
     */
    @Schema(title = "序号", description = "序号")
    private Integer idx;
    /**
     * 电脑端封面
     */
    @Schema(title = "电脑端封面", description = "电脑端封面")
    private AttachmentVo webappCover;
    /**
     * 移动端封面
     */
    @Schema(title = "移动端封面", description = "移动端封面")
    private AttachmentVo mobileCover;
}
