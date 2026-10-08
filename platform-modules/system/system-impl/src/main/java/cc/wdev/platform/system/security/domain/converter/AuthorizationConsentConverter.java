package cc.wdev.platform.system.security.domain.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AuthorizationConsentConverter {

    AuthorizationConsentConverter INSTANCE = Mappers.getMapper(AuthorizationConsentConverter.class);

}
