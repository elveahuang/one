package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;

/**
 * @author elvea
 */
public interface BaseRoleTypeEnum extends BaseBizTypeEnum {

    /**
     * 获取分组类型
     */
    String getRoleGroupType();

    /**
     * 获取范围类型
     */
    String getRoleScopeType();

    /**
     * 获取业务类型
     */
    default String getBizType() {
        return getCode();
    }

}
