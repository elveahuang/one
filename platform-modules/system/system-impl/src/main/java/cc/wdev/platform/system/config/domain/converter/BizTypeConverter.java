package cc.wdev.platform.system.config.domain.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface BizTypeConverter {
    BizTypeConverter INSTANCE = Mappers.getMapper(BizTypeConverter.class);
}
