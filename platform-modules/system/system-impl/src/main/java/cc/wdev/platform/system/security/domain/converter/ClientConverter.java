package cc.wdev.platform.system.security.domain.converter;

import cc.wdev.platform.system.security.domain.dto.ClientDto;
import cc.wdev.platform.system.security.domain.entity.ClientEntity;
import cc.wdev.platform.system.security.domain.form.ClientForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface ClientConverter {

    ClientConverter INSTANCE = Mappers.getMapper(ClientConverter.class);

    ClientDto entity2Dto(ClientEntity entity);

    ClientEntity formToEntity(ClientForm form);

    /**
     * ClientDto 对应 OAuth2 的 RegisteredClient，没有 description 概念，显式忽略以免 MapStruct 告警
     */
    @Mapping(target = "description", ignore = true)
    ClientForm dto2Form(ClientDto dto);

}
