package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * 智能体关联表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_ai_relation")
public class AiRelationEntity extends BaseTenantEntity {

    /**
     * 业务实体ID
     * 1. 智能体ID
     * 2. 知识库ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(title = "业务实体ID", description = "业务实体ID")
    private Long entityId;

    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;

    /**
     * 业务ID
     * 1. 模型ID
     * 2. 工具ID
     * 3. MCP服务ID
     * 4. 知识库ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;

}
