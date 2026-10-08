package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.enums.BaseEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.List;
import java.util.Set;

/**
 * @author elvea
 */
@Slf4j
public abstract class ClassUtils extends org.springframework.util.ClassUtils {

    @SuppressWarnings("unchecked")
    public static <T extends BaseEnum<?>> @NonNull List<T> getEnumClass(String basePackage, Class<T> enumCLass) {
        List<T> enumList = Lists.newArrayList();

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(enumCLass));

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(basePackage);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                if (StringUtils.isNotBlank(beanDefinition.getBeanClassName())) {
                    Class<?> clazz = forName(beanDefinition.getBeanClassName(), getDefaultClassLoader());
                    if (!clazz.isInterface() && clazz.isEnum()) {
                        Object[] constants = clazz.getEnumConstants();
                        for (Object object : constants) {
                            enumList.add((T) object);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to find enum.", e);
            }
        }
        return enumList;
    }

}
