package cc.wdev.platform.commons.utils;


import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author elvea
 */
public abstract class GenericsUtils {

    /**
     * 获取泛型父接口指定下标的参数类型
     *
     * @param clazz        泛型类
     * @param genericClazz 泛型接口
     * @param index        参数下标
     * @param <T>          参数类型
     * @return 泛型父接口指定下标的参数类型
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperGenericType(Class<?> clazz, Class<?> genericClazz, int index) {
        return (Class<T>) ResolvableType.forClass(clazz).as(genericClazz).getGeneric(index).resolve();
    }

    /**
     * 获取泛型父类指定下标的参数类型
     *
     * @param clazz 泛型类
     * @param index 参数下标
     * @param <T>   参数类型
     * @return 泛型父类指定下标的参数类型
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperGenericType(Class<?> clazz, int index) {
        return (Class<T>) ResolvableType.forClass(clazz).getSuperType().getGeneric(index).resolve();
    }

    /**
     * 获取泛型父类指定下标的参数类型
     *
     * @param clazz 泛型类
     * @param index 参数下标
     * @param <T>   参数类型
     * @return 泛型父类指定下标的参数类型
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperClassGenericType(Class<?> clazz, int index) {
        Type genericType = clazz.getGenericSuperclass();
        if (genericType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genericType).getActualTypeArguments();
            if (index >= params.length || index < 0) {
                throw new RuntimeException("invalid index.");
            }
            if (!(params[index] instanceof Class)) {
                return null;
            }
            return (Class<T>) params[index];
        }
        return null;
    }

    /**
     * 获取泛型父接口指定下标的参数类型
     *
     * @param clazz                 泛型类
     * @param genericInterfaceClazz 泛型接口
     * @param index                 参数下标
     * @param <T>                   参数类型
     * @return 泛型父接口指定下标的参数类型
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperInterfacesGenericType(Class<?> clazz, Class<?> genericInterfaceClazz, int index) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericType : genericInterfaces) {
            if (genericType instanceof ParameterizedType parameterizedType) {
                String parameterizedTypeName = parameterizedType.getRawType().getTypeName();
                if (parameterizedTypeName.equalsIgnoreCase(genericInterfaceClazz.getTypeName())) {
                    Type[] params = parameterizedType.getActualTypeArguments();
                    if (index >= params.length || index < 0) {
                        throw new RuntimeException("invalid index.");
                    }
                    if (!(params[index] instanceof Class)) {
                        return null;
                    }
                    return (Class<T>) params[index];
                }
            }
        }
        return null;
    }

}
