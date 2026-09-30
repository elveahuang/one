package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiSessionEventRequest extends PageRequest {
    /**
     * userId
     */
    private Long userId;

    /**
     * Session ID
     */
    @Schema(description = "Session ID")
    private String sessionId;

    /**
     * 会话id
     */
    @Schema(description = "会话id")
    private Long aiSessionId;
}
