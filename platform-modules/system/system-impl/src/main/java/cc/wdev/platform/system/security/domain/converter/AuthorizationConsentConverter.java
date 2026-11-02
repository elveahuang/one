package cc.wdev.platform.system.security.domain.converter;

import cc.wdev.platform.system.security.domain.dto.AuthorizationConsentDto;
import cc.wdev.platform.system.security.domain.entity.AuthorizationConsentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AuthorizationConsentConverter {

    AuthorizationConsentConverter INSTANCE = Mappers.getMapper(AuthorizationConsentConverter.class);

    AuthorizationConsentDto entity2Dto(AuthorizationConsentEntity entity);

    AuthorizationConsentEntity dto2Entity(AuthorizationConsentDto dto);

}
