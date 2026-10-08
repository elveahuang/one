package cc.wdev.webapp.jpa.domain.entity;

import cc.wdev.platform.commons.data.jpa.domain.BaseEntity;
import cc.wdev.platform.commons.data.jpa.tenant.TenantId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sp_user")
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class JpaUserEntity extends BaseEntity {
    /**
     * 租户
     */
    @TenantId
    private Long tenantId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 版本号
     */
    @Version
    private Long version;
    /**
     * 用户名
     */
    private String bizType;
    /**
     * 租户
     */
    private Long bizId;

    @PrePersist
    public void prePersist() {
        this.version = 0L;
    }

}
