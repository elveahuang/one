package cc.wdev.platform.system.site.domain.form;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementForm implements Serializable {
    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "公告资讯ID")
    protected Long id;
    /**
     * 标题
     */
    @NotEmpty
    @Schema(description = "公告资讯标题")
    private String title;
    /**
     * 内容
     */
    @NotEmpty
    @Schema(description = "公告资讯内容")
    private String content;
    /**
     * 发布状态
     */
    @Schema(description = "公告资讯发布状态")
    private Integer status;
    /**
     * 允许评论类型
     */
    @Schema(description = "公告资讯允许评论类型")
    private Integer allowCommentInd;
    /**
     * 备注
     */
    @Schema(description = "公告资讯备注")
    private String description;
    /**
     * 发布期限-开始时间
     */
    @Schema(description = "公告资讯发布期限-开始时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime startDatetime;
    /**
     * 发布期限-结束时间
     */
    @Schema(description = "公告资讯发布期限-结束时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime endDatetime;
}
