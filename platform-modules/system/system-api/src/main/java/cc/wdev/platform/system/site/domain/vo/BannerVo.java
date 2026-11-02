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

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "宣传栏對象")
public class BannerVo {
    /**
     * 主键
     */
    @Schema(title = "宣传栏ID", description = "宣传栏ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 标题
     */
    @Schema(title = "宣传栏标题", description = "宣传栏标题")
    private String title;
    /**
     * 详情
     */
    @Schema(title = "宣传栏详情", description = "宣传栏详情")
    private String details;
    /**
     * 备注
     */
    @Schema(title = "宣传栏备注", description = "宣传栏备注")
    private String description;
    /**
     * 序号
     */
    @Schema(title = "宣传栏序号", description = "宣传栏序号")
    private Integer idx;
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
     * 类型
     */
    @Schema(title = "宣传栏类型", description = "宣传栏类型")
    private RelationVo<?> type;
    /**
     * 封面
     */
    @Schema(title = "宣传栏封面", description = "宣传栏封面")
    private AttachmentVo cover;
    /**
     * 移动端封面
     */
    @Schema(title = "宣传栏移动端封面", description = "宣传栏移动端封面")
    private AttachmentVo mobileCover;
    /**
     * 状态
     */
    @Schema(title = "宣传栏状态", description = "宣传栏状态")
    private Integer status;
}
