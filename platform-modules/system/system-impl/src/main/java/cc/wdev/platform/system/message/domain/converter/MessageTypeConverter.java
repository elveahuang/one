package cc.wdev.platform.system.message.domain.converter;

import cc.wdev.platform.system.message.domain.entity.MessageTypeEntity;
import cc.wdev.platform.system.message.domain.vo.MessageTypeVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface MessageTypeConverter {

    MessageTypeConverter INSTANCE = Mappers.getMapper(MessageTypeConverter.class);

    @Mapping(target = "items", ignore = true)
    MessageTypeVo entityToVo(MessageTypeEntity entity);

}
