package cc.wdev.platform.commons.utils;

import cn.hutool.core.util.ArrayUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ArrayUtils extends ArrayUtil {

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return (null != array && array.length != 0);
    }

    public static <T> T[] nvl(@Nullable T[] array, @NonNull T[] defaultArray) {
        return isNotEmpty(array) ? array : defaultArray;
    }

}
