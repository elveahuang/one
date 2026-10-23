package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.system.core.domain.dto.PositionDto;
import cc.wdev.platform.system.core.domain.dto.PositionSaveDto;
import cc.wdev.platform.system.core.domain.entity.PositionEntity;
import cc.wdev.platform.system.core.domain.vo.PositionVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author elvea
 */
@Mapper
public interface PositionConverter {

    PositionConverter INSTANCE = Mappers.getMapper(PositionConverter.class);

    @Mapping(target = "rootInd", ignore = true)
    @Mapping(target = "defaultInd", ignore = true)
    PositionEntity saveDto2Entity(PositionSaveDto saveDto);

    @Mapping(target = "rootInd", ignore = true)
    @Mapping(target = "defaultInd", ignore = true)
    void updateEntityFromSaveDto(PositionSaveDto saveDto, @MappingTarget PositionEntity entity);

    PositionDto entity2Dto(PositionEntity entity);

    @Mapping(target = "children", ignore = true)
    PositionVo entity2Vo(PositionEntity entity);

    List<PositionVo> entityList2VoList(List<PositionEntity> entityList);

}
