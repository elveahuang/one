package cc.wdev.platform.commons.enums;

import cc.wdev.platform.commons.utils.ObjectUtils;

import java.util.EnumSet;

/**
 * 业务类型枚举
 *
 * @author elvea
 */
public interface BaseBizTypeEnum extends BaseEnum<String> {

    /**
     * 获取业务分组
     */
    String getGroup();

    /**
     * 获取业务范围
     */
    default String getScope() {
        return null;
    }

    /**
     * 获取业务配置
     */
    default BaseBizTypeConfig getConfig() {
        return null;
    }

    /**
     * 获取业务编号
     */
    default String getCode() {
        return getValue();
    }

    /**
     * 获取多语言文本
     */
    default String getLabelKey() {
        return ("label__" + getGroup() + "__" + getValue()).toLowerCase();
    }

    default String getKey() {
        return (getGroup() + "_" + getValue()).toUpperCase();
    }

    static <E extends Enum<E> & BaseBizTypeEnum> E getEnumByCode(Object code, Class<E> clazz) {
        return getEnumByCode(code, clazz, null);
    }

    static <E extends Enum<E> & BaseBizTypeEnum> E getEnumByCode(Object code, Class<E> clazz, E defaultValue) {
        EnumSet<E> enumSet = EnumSet.allOf(clazz);
        return enumSet.stream()
            .filter(e -> ObjectUtils.equals(e.getCode(), code))
            .findFirst()
            .orElse(defaultValue);
    }

}
