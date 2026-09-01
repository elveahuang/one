package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
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
@TableName("sys_package_log")
public class PackageLogEntity extends BaseTenantEntity {
    /**
     * 业务类型
     */
    private Long bizType;
    /**
     * 实体ID
     */
    private Long bizId;
    /**
     * 套餐ID
     */
    private Long packageId;
    /**
     * 关联订单ID
     */
    private Long orderId;
    /**
     * 额度
     */
    private Long quota;
    /**
     * 备注
     */
    private String description;
    /**
     * 日志类型
     */
    private Integer logType;
}
