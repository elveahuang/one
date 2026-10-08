package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 身份类型
 * USER     - 用户体系，用户体系用于后台系统
 * AGENT    - 智能体｜机器人
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum EntityTypeEnum implements BaseEnum<Integer> {
    USER(1, "USER", "用户体系"),
    AGENT(2, "AGENT", "智能体"),
    NONE(0, "NONE", "未知身份");

    private final Integer value;
    private final String code;
    private final String description;

    /**
     * 获取身份类型
     */
    public static EntityTypeEnum getEntityType(int type) {
        EntityTypeEnum[] ts = EntityTypeEnum.values();
        for (EntityTypeEnum t : ts) {
            if (t.getValue() == type) {
                return t;
            }
        }
        return NONE;
    }

    /**
     * 获取身份类型
     */
    public static EntityTypeEnum getEntityType(String code) {
        EntityTypeEnum[] ts = EntityTypeEnum.values();
        for (EntityTypeEnum t : ts) {
            if (t.getCode().equalsIgnoreCase(code)) {
                return t;
            }
        }
        return NONE;
    }

}
