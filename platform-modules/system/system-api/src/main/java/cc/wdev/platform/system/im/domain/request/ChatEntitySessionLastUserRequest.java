package cc.wdev.platform.system.im.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "获取最近的实体会话用户信息")
public class ChatEntitySessionLastUserRequest extends Request {

    /**
     * 业务类型
     */
    @Schema(title = "用户ID")
    private String bizType;

    /**
     * 用户ID数组
     */
    @Schema(title = "用户ID数组")
    private List<Long> userIds;

    /**
     * 业务ID数组
     */
    @Schema(title = "业务ID数组")
    private List<Long> bizIds;
}
