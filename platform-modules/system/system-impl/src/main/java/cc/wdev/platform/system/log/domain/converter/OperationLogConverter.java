package cc.wdev.platform.system.log.domain.converter;

import cc.wdev.platform.commons.core.log.domain.OperationLogDto;
import cc.wdev.platform.system.log.domain.entity.OperationLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface OperationLogConverter {

    OperationLogConverter INSTANCE = Mappers.getMapper(OperationLogConverter.class);

    OperationLogEntity dto2Entity(OperationLogDto dto);

}
