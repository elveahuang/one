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
@TableName("sys_entity_open_id")
public class EntityOpenIdEntity extends BaseTenantEntity {
    /**
     * 用户ID
     */
    private String bizType;
    /**
     * 用户ID
     */
    private Long bizId;
    /**
     * OpenID
     */
    private String openId;
    /**
     * unionId
     */
    private String unionId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 头像地址
     */
    private String avatar;
    /**
     * 来源
     */
    private Integer source;
    /**
     * 状态
     */
    private Integer status;
}
