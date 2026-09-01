package cc.wdev.platform.system.config.domain.converter;

import cc.wdev.platform.system.config.domain.entity.ConfigEntity;
import cc.wdev.platform.system.config.domain.request.ConfigSaveRequest;
import cc.wdev.platform.system.config.domain.vo.ConfigVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface ConfigConverter {

    ConfigConverter INSTANCE = Mappers.getMapper(ConfigConverter.class);

    ConfigVo entityToDto(ConfigEntity entity);

    @Mapping(target = "label", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "source", ignore = true)
    ConfigEntity formToEntity(ConfigSaveRequest form);

}
