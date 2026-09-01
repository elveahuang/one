package cc.wdev.platform.system.site.domain.entity;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_announcement")
@Schema(description = "公告资讯实体")
public class AnnouncementEntity extends BaseTenantEntity {
    /**
     * 标题
     */
    @Schema(title = "标题", description = "标题")
    private String title;
    /**
     * 内容
     */
    @Schema(title = "内容", description = "内容")
    private String content;
    /**
     * 发布状态
     */
    @Schema(title = "发布状态", description = "发布状态")
    private Integer status;
    /**
     * 允许评论类型
     */
    @Schema(title = "允许评论类型", description = "允许评论类型")
    private Integer allowCommentInd;
    /**
     * 备注
     */
    @Schema(title = "备注", description = "备注")
    private String description;
    /**
     * 发布期限-开始时间
     */
    @Schema(title = "发布期限-开始时间", description = "发布期限-开始时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime startDatetime;
    /**
     * 发布期限-结束时间
     */
    @Schema(title = "发布期限-结束时间", description = "发布期限-结束时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime endDatetime;
}
