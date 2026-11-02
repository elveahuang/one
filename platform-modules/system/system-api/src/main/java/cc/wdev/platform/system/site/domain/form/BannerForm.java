package cc.wdev.platform.system.site.domain.form;

import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.dict.domain.vo.DictVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BannerForm implements Serializable {
    /**
     * ID
     */
    @Schema(title = "宣传栏ID", description = "宣传栏ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 标题
     */
    @Schema(title = "标题", description = "宣传栏标题")
    @NotBlank(message = "标题不能为空")
    private String title;
    /**
     * 详情
     */
    @Schema(title = "详情", description = "宣传栏详情")
    private String details;
    /**
     * 描述
     */
    @Schema(title = "描述", description = "宣传栏描述")
    private String description;
    /**
     * 序号
     */
    @Schema(title = "序号", description = "宣传栏序号")
    private Integer idx;
    /**
     * 链接类型
     */
    @Schema(title = "链接类型", description = "宣传栏链接类型")
    private String linkType;
    /**
     * 链接参数
     */
    @Schema(title = "链接参数", description = "宣传栏链接参数")
    private String linkParams;
    /**
     * 外部链接地址
     */
    @Schema(title = "外部链接地址", description = "宣传栏外部链接地址")
    private String linkExternalUrl;
    /**
     * 开始时间
     */
    @Schema(title = "开始时间", description = "宣传栏开始时间")
    private LocalDateTime startDatetime;
    /**
     * 结束时间
     */
    @Schema(title = "结束时间", description = "宣传栏结束时间")
    private LocalDateTime endDatetime;
    /**
     * 状态
     */
    @Schema(title = "状态", description = "宣传栏状态")
    private Integer status;
    /**
     * 类型
     */
    @Schema(title = "类型", description = "宣传栏类型")
    private RelationVo<DictVo> type;
    /**
     * 封面
     */
    @Schema(title = "封面", description = "宣传栏封面")
    private AttachmentVo cover;
    /**
     * 移动端封面
     */
    @Schema(title = "移动端封面", description = "宣传栏移动端封面")
    private AttachmentVo mobileCover;
}
