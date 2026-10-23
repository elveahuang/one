package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.system.core.domain.dto.OrganizationDto;
import cc.wdev.platform.system.core.domain.dto.OrganizationSaveDto;
import cc.wdev.platform.system.core.domain.entity.OrganizationEntity;
import cc.wdev.platform.system.core.domain.vo.OrganizationVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author elvea
 */
@Mapper
public interface OrganizationConverter {

    OrganizationConverter INSTANCE = Mappers.getMapper(OrganizationConverter.class);

    @Mapping(target = "rootInd", ignore = true)
    @Mapping(target = "defaultInd", ignore = true)
    OrganizationEntity saveDto2Entity(OrganizationSaveDto saveDto);

    @Mapping(target = "rootInd", ignore = true)
    @Mapping(target = "defaultInd", ignore = true)
    void updateEntityFromSaveDto(OrganizationSaveDto saveDto, @MappingTarget OrganizationEntity entity);

    OrganizationDto entity2Dto(OrganizationEntity entity);

    @Mapping(target = "children", ignore = true)
    OrganizationVo entity2Vo(OrganizationEntity entity);

    List<OrganizationVo> entityList2VoList(List<OrganizationEntity> entityList);

}
