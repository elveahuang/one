package cc.wdev.platform.commons.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
public abstract class ConvertUtils {

    /**
     * convert
     *
     * @param source 来源
     * @param target 目标类型
     * @param <T>    目标类型
     * @return T
     */
    public static <T> T convert(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        T targetObject = null;
        try {
            targetObject = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, targetObject);
        } catch (Exception e) {
            log.error("convert error ", e);
        }
        return targetObject;
    }

    /**
     * convert
     *
     * @param sourceList 来源
     * @param target     目标类
     * @param <T>        目标类型
     * @return List
     */
    public static <T> List<T> convert(Collection<?> sourceList, Class<T> target) {
        if (sourceList == null) {
            return null;
        }

        List<T> targetList = Lists.newArrayList();
        try {
            if (CollectionUtils.isNotEmpty(sourceList)) {
                for (Object source : sourceList) {
                    T targetObject = target.getDeclaredConstructor().newInstance();
                    BeanUtils.copyProperties(source, targetObject);
                    targetList.add(targetObject);
                }
            }
        } catch (Exception e) {
            log.error("convert error ", e);
        }
        return targetList;
    }

    public static <S, T> void copyProperties(S source, T target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

}
