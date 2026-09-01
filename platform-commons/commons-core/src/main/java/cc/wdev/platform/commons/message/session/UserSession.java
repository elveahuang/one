package cc.wdev.platform.commons.message.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {
    /**
     * 会话ID
     */
    private String sid;
    /**
     * 连接ID
     */
    private String cid;
    /**
     * 用户ID
     */
    private Long uid;
    /**
     * 用户类型
     */
    private String uType;
    /**
     * 租户ID
     */
    private Long tid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 最后活动时间
     */
    private Long last;
}
