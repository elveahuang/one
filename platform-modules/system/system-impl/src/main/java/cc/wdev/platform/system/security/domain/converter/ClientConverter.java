package cc.wdev.platform.system.security.domain.converter;

import cc.wdev.platform.system.security.domain.dto.ClientDto;
import cc.wdev.platform.system.security.domain.entity.ClientEntity;
import cc.wdev.platform.system.security.domain.form.ClientForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface ClientConverter {

    ClientConverter INSTANCE = Mappers.getMapper(ClientConverter.class);

    ClientDto entity2Dto(ClientEntity entity);

    ClientEntity formToEntity(ClientForm form);

}
