package cc.wdev.platform.system.ai.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 知识条目分页查询请求
 *
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识条目分页查询请求")
public class AiKbItemSearchRequest extends PageRequest {

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID")
    private Long kbId;

    /**
     * 知识库编号
     */
    @Schema(title = "知识库编号")
    private String kbCode;

    /**
     * 业务类型（TEXT/QA/STRUCTURED/DOCUMENT）
     */
    @Schema(title = "业务类型")
    private String bizType;

    /**
     * 关键词
     */
    @Schema(title = "关键词")
    private String q;

    /**
     * 向量化状态（1 待处理 / 2 处理中 / 3 已完成 / 4 失败）
     */
    @Schema(title = "向量化状态")
    private Integer vectorized;

}
