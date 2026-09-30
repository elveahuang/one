package cc.wdev.platform.system.core.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "邀请统计参数")
public class InviteStatisticRequest extends Request {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户ID数组")
    private Collection<Long> userIds;

    @Schema(description = "邀请码")
    private String inviteCode;

}
