package cc.wdev.platform.system.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "知识库元数据", description = "知识库元数据")
public class AiKbMetadataRequest implements Serializable {

    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户ID")
    private Long tenantId;

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID", description = "知识库ID")
    private Long kbId;

    /**
     * 知识条目ID
     */
    @Schema(title = "知识条目ID", description = "知识条目ID")
    private Long kbItemId;

    /**
     * 类型
     */
    @Schema(title = "类型", description = "类型")
    private String kbItemType;

    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String kbItemBizType;

    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    private Long kbItemBizId;

    /**
     * 知识分片ID
     */
    @Schema(title = "知识分片ID", description = "知识分片ID")
    private Long kbChunkId;

    /**
     * 知识分片索引
     */
    @Schema(title = "知识分片索引", description = "知识分片索引")
    private Integer kbChunkIndex;


}
