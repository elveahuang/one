package cc.wdev.platform.system.message.domain.converter;

import cc.wdev.platform.system.message.domain.entity.MessageChannelEntity;
import cc.wdev.platform.system.message.domain.vo.MessageChannelVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface MessageChannelConverter {

    MessageChannelConverter INSTANCE = Mappers.getMapper(MessageChannelConverter.class);

    MessageChannelVo entityToVo(MessageChannelEntity entity);

}
