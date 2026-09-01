package cc.wdev.platform.system.dict.domain.converter;

import cc.wdev.platform.system.dict.domain.entity.DictEntity;
import cc.wdev.platform.system.dict.domain.request.DictSaveRequest;
import cc.wdev.platform.system.dict.domain.vo.DictVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface DictConverter {

    DictConverter INSTANCE = Mappers.getMapper(DictConverter.class);

    @Mapping(target = "extra", ignore = true)
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "referenceId", ignore = true)
    DictEntity requestToEntity(DictSaveRequest request);

    DictVo entity2Vo(DictEntity entity);

}
