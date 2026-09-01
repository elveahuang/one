package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_entity_package")
public class EntityPackageEntity extends BaseTenantEntity {
    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 权限ID
     */
    private Long packageId;
    /**
     * 实体ID
     */
    private Long entityId;
    /**
     * 会员试用开始时间
     */
    private LocalDateTime trialStartDate;
    /**
     * 会员试用结束时间
     */
    private LocalDateTime trialEndDate;
    /**
     * 会员注册时间
     */
    private LocalDateTime registrationDate;
    /**
     * 会员到期时间
     */
    private LocalDateTime expirationDate;
    /**
     * 备注说明
     */
    private String description;
}
