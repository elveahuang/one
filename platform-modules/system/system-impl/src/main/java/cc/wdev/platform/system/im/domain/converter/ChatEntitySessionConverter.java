package cc.wdev.platform.system.im.domain.converter;

import cc.wdev.platform.system.im.domain.entity.ChatEntitySessionEntity;
import cc.wdev.platform.system.im.domain.vo.ChatEntitySessionVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatEntitySessionConverter {

    ChatEntitySessionConverter INSTANCE = Mappers.getMapper(ChatEntitySessionConverter.class);

    @Mapping(target = "lastReadTime", ignore = true)
    ChatEntitySessionVo entityToVo(ChatEntitySessionEntity entity);

}
