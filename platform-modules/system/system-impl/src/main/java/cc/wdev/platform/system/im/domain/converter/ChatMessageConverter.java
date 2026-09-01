package cc.wdev.platform.system.im.domain.converter;

import cc.wdev.platform.system.im.domain.entity.ChatMessageEntity;
import cc.wdev.platform.system.im.domain.request.ChatMessageSaveRequest;
import cc.wdev.platform.system.im.domain.vo.ChatMessageVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author erden
 */
@Mapper
public interface ChatMessageConverter {

    ChatMessageConverter INSTANCE = Mappers.getMapper(ChatMessageConverter.class);

    @Mapping(target = "content", ignore = true)
    @Mapping(target = "extra", ignore = true)
    ChatMessageVo entityToVo(ChatMessageEntity entity);

    @Mapping(target = "messageContentType", ignore = true)
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "status", ignore = true)
    ChatMessageEntity requestToEntity(ChatMessageSaveRequest request);

}
