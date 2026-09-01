package cc.wdev.platform.system.log.domain.entity;

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
@TableName("sys_login_log")
public class LoginLogEntity extends BaseTenantEntity {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户登录主机
     */
    private String host;
    /**
     * User Agent
     */
    private String userAgent;
    /**
     * 客户端编号
     */
    private String clientId;
    /**
     * 客户端名称
     */
    private String clientName;
    /**
     * 客户端版本
     */
    private String clientVersion;
    /**
     * 详情
     */
    private String details;
    /**
     * 异常
     */
    private String exception;
    /**
     * 登录状态
     */
    private Integer success;

}
