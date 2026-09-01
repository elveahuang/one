package cc.wdev.platform.system.im.domain.converter;

import cc.wdev.platform.system.im.domain.entity.ChatMessageContentEntity;
import cc.wdev.platform.system.im.domain.request.ChatMessageContentSaveRequest;
import cc.wdev.platform.system.im.domain.vo.ChatMessageContentVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatMessageContentConverter {

    ChatMessageContentConverter INSTANCE = Mappers.getMapper(ChatMessageContentConverter.class);

    ChatMessageContentVo entityToVo(ChatMessageContentEntity entity);

    void saveReq2Entity(ChatMessageContentSaveRequest req, @MappingTarget ChatMessageContentEntity entity);
}
