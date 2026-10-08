package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.system.core.domain.dto.AuthorityDto;
import cc.wdev.platform.system.core.domain.entity.AuthorityEntity;
import cc.wdev.platform.system.core.domain.vo.AuthorityVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author elvea
 */
@Mapper
public interface AuthorityConverter {

    AuthorityConverter INSTANCE = Mappers.getMapper(AuthorityConverter.class);

    List<AuthorityDto> entityListToDtoList(List<AuthorityEntity> entityList);

    AuthorityVo entityToVo(AuthorityEntity entity);

}
