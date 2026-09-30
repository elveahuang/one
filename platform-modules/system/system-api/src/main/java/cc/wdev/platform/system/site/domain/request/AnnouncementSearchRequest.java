package cc.wdev.platform.system.site.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AnnouncementSearchRequest extends PageRequest {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    private Long tenantId;

    /**
     * 是否允许评论
     */
    @Schema(title = "是否允许评论", defaultValue = "1", description = "是否允许评论")
    private Integer allowCommentInd;
}
