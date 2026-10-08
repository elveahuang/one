package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * AI 用量分页查询请求
 *
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI 用量分页查询请求")
public class AiUsageSearchRequest extends PageRequest {

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID")
    private Long kbId;

    /**
     * 用户ID
     */
    @Schema(title = "用户ID")
    private Long userId;

    /**
     * 用量类型（CHAT / SEARCH / EMBEDDING / RERANK）
     */
    @Schema(title = "用量类型")
    private String usageType;

}
