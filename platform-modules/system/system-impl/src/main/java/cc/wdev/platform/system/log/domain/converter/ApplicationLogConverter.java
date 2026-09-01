package cc.wdev.platform.system.log.domain.converter;

import cc.wdev.platform.commons.core.log.domain.ApplicationLogDto;
import cc.wdev.platform.system.log.domain.entity.ApplicationLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface ApplicationLogConverter {

    ApplicationLogConverter INSTANCE = Mappers.getMapper(ApplicationLogConverter.class);

    ApplicationLogEntity dto2Entity(ApplicationLogDto dto);

}
