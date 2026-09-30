package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Slf4j
public class MapStructUtils {

    private static volatile String model = MappingConstants.ComponentModel.DEFAULT;

    public static void setModel(String model) {
        MapStructUtils.model = model;
    }

    public static void init(String model) {
        setModel(model);
    }

    public static <C> C getConverter(Class<C> clazz) {
        if (MappingConstants.ComponentModel.SPRING.equals(model)) {
            return SpringUtils.getBean(clazz);
        }
        return Mappers.getMapper(clazz);
    }

}
