package cc.wdev.platform.system.site.domain.vo.webapp;

import cc.wdev.platform.system.storage.domain.vo.webapp.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

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
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;
    /**
     * 链接类型
     */
    @Schema(title = "链接类型", description = "链接类型")
    private String linkType;
    /**
     * 链接参数
     */
    @Schema(title = "链接参数", description = "链接参数")
    private String linkParams;
    /**
     * 外部链接地址
     */
    @Schema(title = "外部链接地址", description = "外部链接地址")
    private String linkExternalUrl;
    /**
     * 开始时间
     */
    @Schema(title = "开始时间", description = "开始时间")
    private LocalDateTime startDatetime;
    /**
     * 结束时间
     */
    @Schema(title = "结束时间", description = "结束时间")
    private LocalDateTime endDatetime;
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
