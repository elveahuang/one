package cc.wdev.platform.system.ai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "知识条目获取请求", description = "知识条目获取请求")
public class AiKbItemResolveRequest implements Serializable {

    /**
     * 知识条目ID
     */
    @Schema(title = "知识条目ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;

    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    private Long bizId;

    /**
     * 知识库ID
     */
    @Schema(title = "知识库ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long kbId;

    /**
     * 知识库编号
     */
    @Schema(description = "知识库编号")
    private String kbCode;

}
