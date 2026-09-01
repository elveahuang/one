package cc.wdev.platform.system.region.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_region_relation")
public class RegionRelationEntity extends SimpleTenantEntity {
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 业务ID
     */
    private Long bizId;
    /**
     * 地址ID
     */
    private Long regionId;
}
