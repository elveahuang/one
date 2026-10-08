package cc.wdev.platform.commons.security.domain;

/**
 * @author elvea
 */
public interface CustomUserDetails {

    /**
     * 获取用户ID
     */
    Long getUid();

    /**
     * 获取租户ID
     */
    Long getTid();

}
