package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum EntityRelationBizTypeEnum implements BaseEnum<String> {
    ORG_PARENT_ORG("ORG_PARENT_ORG", "组织<->组织关联"),
    POS_PARENT_POS("POS_PARENT_POS", "岗位<->岗位关联"),
    LVL_PARENT_LVL("LVL_PARENT_LVL", "职级<->职级关联"),
    USR_CURRENT_ORG("USR_CURRENT_ORG", "用户<->组织关联"),
    USR_CURRENT_POS("USR_CURRENT_POS", "用户<->岗位关联"),
    USR_CURRENT_LVL("USR_CURRENT_LVL", "用户<->职级关联");

    private final String value;
    private final String description;
}
