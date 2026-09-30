package cc.wdev.platform.system.im.domain.request;

import cc.wdev.platform.commons.web.request.IdsRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "获取实体会话信息")
public class ChatEntitySessionSearchRequest extends IdsRequest {
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private Collection<Long> chatSessionIds;
    /**
     * 用户ID
     */
    @Schema(title = "用户ID")
    private Collection<Long> userIds;
}
