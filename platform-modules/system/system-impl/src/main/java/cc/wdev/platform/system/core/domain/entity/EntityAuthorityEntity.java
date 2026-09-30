package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_entity_authority")
public class EntityAuthorityEntity extends SimpleTenantEntity {
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 实体ID
     */
    private Long entityId;
    /**
     * 权限ID
     */
    private Long authorityId;
}
