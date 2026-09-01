package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 知识库分页查询请求
 *
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库查询请求")
public class AiKbSearchRequest extends PageRequest {

    /**
     * 关键词（编号/名称）
     */
    @Schema(title = "关键词", description = "关键词")
    private String q;

    /**
     * 状态
     */
    @Schema(title = "状态", description = "状态")
    private Integer status;

}
