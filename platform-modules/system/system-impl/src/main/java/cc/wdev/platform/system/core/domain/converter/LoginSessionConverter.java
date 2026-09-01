package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.system.core.domain.dto.LoginSessionDto;
import cc.wdev.platform.system.core.domain.entity.LoginSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface LoginSessionConverter {

    LoginSessionConverter INSTANCE = Mappers.getMapper(LoginSessionConverter.class);

    @Mapping(target = "actionType", ignore = true)
    LoginSessionDto entity2Dto(LoginSessionEntity entity);

    @Mapping(target = "startYear", ignore = true)
    @Mapping(target = "startMonth", ignore = true)
    @Mapping(target = "startDay", ignore = true)
    @Mapping(target = "startHour", ignore = true)
    @Mapping(target = "startMinute", ignore = true)
    @Mapping(target = "lastAccessYear", ignore = true)
    @Mapping(target = "lastAccessMonth", ignore = true)
    @Mapping(target = "lastAccessDay", ignore = true)
    @Mapping(target = "lastAccessHour", ignore = true)
    @Mapping(target = "lastAccessMinute", ignore = true)
    LoginSessionEntity dto2Entity(LoginSessionDto dto);

}
