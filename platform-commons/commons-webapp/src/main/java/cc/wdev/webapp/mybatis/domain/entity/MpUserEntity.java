package cc.wdev.webapp.mybatis.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_user")
public class MpUserEntity extends BaseEntity {
    /**
     * 租户
     */
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
}
