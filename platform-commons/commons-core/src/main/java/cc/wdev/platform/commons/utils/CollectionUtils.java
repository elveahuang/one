package cc.wdev.platform.commons.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * @author elvea
 */
public abstract class CollectionUtils extends org.springframework.util.CollectionUtils {

    public static <E> Collection<E> nvl(@Nullable Collection<E> collection) {
        return nvl(collection, Collections.emptyList());
    }

    public static <E> Collection<E> nvl(@Nullable Collection<E> collection, @NonNull Collection<E> defaultCollection) {
        return isNotEmpty(collection) ? collection : defaultCollection;
    }

    public static Map<?, ?> nvl(@Nullable Map<?, ?> map) {
        return nvl(map, Maps.newHashMap());
    }

    public static Map<?, ?> nvl(@Nullable Map<?, ?> map, @NonNull Map<?, ?> defaultMap) {
        return isNotEmpty(map) ? map : defaultMap;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <E> List<E> toArrayList(final Iterable<E> iterable) {
        return Lists.newArrayList(iterable);
    }

    public static <E> List<E> toArrayList(final Iterator<E> iterator) {
        return Lists.newArrayList(iterator);
    }

    public static Long[] toLongArray(final Collection<Long> collection) {
        return collection.toArray(new Long[0]);
    }

}
