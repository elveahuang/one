package cc.wdev.platform.commons.enums;

import cc.wdev.platform.commons.utils.ObjectUtils;

import java.io.Serializable;
import java.util.EnumSet;

import static cc.wdev.platform.commons.constants.GlobalConstants.I18N_LABEL_GROUP;

/**
 * 基础枚举接口
 *
 * @author elvea
 */
public interface BaseEnum<T extends Serializable> {

    T getValue();

    String getDescription();

    /**
     * 获取多语言文本
     */
    default String getLabelKey() {
        return ("label__" + getValue()).toLowerCase();
    }

    /**
     * 获取多语言文本分组
     */
    default String getLabelGroup() {
        return I18N_LABEL_GROUP;
    }

    static <E extends Enum<E> & BaseEnum<?>> E getEnumByValue(Object value, Class<E> clazz) {
        return getEnumByValue(value, clazz, null);
    }

    static <E extends Enum<E> & BaseEnum<?>> E getEnumByValue(Object value, Class<E> clazz, E defaultValue) {
        EnumSet<E> enumSet = EnumSet.allOf(clazz);
        return enumSet.stream()
            .filter(e -> ObjectUtils.equals(e.getValue(), value))
            .findFirst()
            .orElse(defaultValue);
    }

}
