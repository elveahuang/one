package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.core.domain.TenantEntity;
import cc.wdev.platform.commons.data.mybatis.domain.AbstractEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * AI Session 元数据索引。
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_session_index")
@Schema(title = "AI Session Index", description = "AI Session 元数据索引")
public class AiSessionIndexEntity extends AbstractEntity implements TenantEntity {

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /**
     * Session ID
     */
    @Schema(description = "Session ID")
    private String sessionId;

    /**
     * 属性名
     */
    @Schema(description = "属性名")
    private String attrName;

    /**
     * 属性值
     */
    @Schema(description = "属性值")
    private String attrValue;

}
