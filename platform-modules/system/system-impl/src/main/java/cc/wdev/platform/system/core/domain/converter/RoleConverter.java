package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.system.core.domain.dto.RoleDto;
import cc.wdev.platform.system.core.domain.entity.RoleEntity;
import cc.wdev.platform.system.core.domain.form.RoleForm;
import cc.wdev.platform.system.core.domain.vo.RoleVo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author elvea
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleConverter {

    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);

    @Mapping(target = "dataScopeType", ignore = true)
    RoleEntity formToEntity(RoleForm form);

    RoleVo entityToVo(RoleEntity entity);

    void formToEntity(RoleForm form, @MappingTarget RoleEntity entity);

    List<RoleDto> entityListToDtoList(List<RoleEntity> entityList);

}
