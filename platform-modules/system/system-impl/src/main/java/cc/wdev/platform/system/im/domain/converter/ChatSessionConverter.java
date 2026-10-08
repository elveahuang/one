package cc.wdev.platform.system.im.domain.converter;

import cc.wdev.platform.system.im.domain.entity.ChatSessionEntity;
import cc.wdev.platform.system.im.domain.request.ChatSaveRequest;
import cc.wdev.platform.system.im.domain.vo.ChatSessionVo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatSessionConverter {

    ChatSessionConverter INSTANCE = Mappers.getMapper(ChatSessionConverter.class);

    @Mapping(target = "topInd", ignore = true)
    @Mapping(target = "collectInd", ignore = true)
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "lastReadMessageId", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "bizObj", ignore = true)
    @Mapping(target = "chatEntitySessionId", ignore = true)
    @Mapping(target = "unsuitableInd", ignore = true)
    ChatSessionVo entityToVo(ChatSessionEntity entity);

    void saveReq2Entity(ChatSaveRequest req, @MappingTarget ChatSessionEntity entity);

}
