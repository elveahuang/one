package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author elvea
 */
public abstract class ObjectUtils extends org.springframework.util.ObjectUtils {

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isValidId(Object object) {
        return switch (object) {
            case Long id -> id > 0;
            case String id -> isValidLongString(id);
            case IdEntity entity -> entity.getId() != null && entity.getId() > 0;
            case null, default -> false;
        };
    }

    public static boolean isInvalidId(Object object) {
        return switch (object) {
            case null -> true;
            case Long id -> id <= 0;
            case String id -> isInvalidLongString(id);
            case IdEntity entity -> entity.getId() == null || entity.getId() <= 0;
            default -> false;
        };
    }

    public static boolean isValidId(Long... arr) {
        if (ArrayUtils.isEmpty(arr)) {
            return false;
        }
        for (Long id : arr) {
            if (!isValidId(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 如果第一个参数为空，则返回第二个参数
     */
    public static <T> T nvl(final T object, final T defaultObject) {
        return isEmpty(object) ? defaultObject : object;
    }

    /**
     *
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 instanceof Number n1 && o2 instanceof Number n2) {
            return n1.longValue() == n2.longValue();
        } else if (o1 instanceof String s1 && o2 instanceof String s2) {
            return StringUtils.isNotEmpty(s1) && StringUtils.isNotEmpty(s2) && s1.equalsIgnoreCase(s2);
        }
        return nullSafeEquals(o1, o2);
    }

    public static void copyNotNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullProperties(source));
    }

    public static String[] getNullProperties(Object object) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(object);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

        Set<String> fieldSet = new HashSet<>();
        for (PropertyDescriptor p : propertyDescriptors) {
            String propertyName = p.getName();
            Object propertyValue = beanWrapper.getPropertyValue(propertyName);
            if (Objects.isNull(propertyValue)) {
                fieldSet.add(propertyName);
            }
        }

        return fieldSet.toArray(new String[0]);
    }

    private static boolean isValidLongString(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            return Long.parseLong(str.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isInvalidLongString(String str) {
        if (StringUtils.isBlank(str)) {
            return true;
        }
        try {
            return Long.parseLong(str.trim()) <= 0;
        } catch (NumberFormatException e) {
            return true;
        }
    }

}
