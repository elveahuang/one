package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_entity_relation")
public class EntityRelationEntity extends SimpleTenantEntity {
    /**
     * 祖先ID
     */
    private Long ancestorId;
    /**
     * 实体ID
     */
    private Long entityId;
    /**
     * 是否直接上级
     */
    private Integer parentInd;
    /**
     * 关联类型
     */
    private String relationType;
    /**
     * 关联路径
     */
    private String relationPath;
    /**
     * 关联层级
     */
    private Integer relationIndex;
}
