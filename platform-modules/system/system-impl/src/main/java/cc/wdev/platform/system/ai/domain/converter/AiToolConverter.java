package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiToolEntity;
import cc.wdev.platform.system.ai.domain.request.AiToolSaveRequest;
import cc.wdev.platform.system.ai.domain.vo.AiToolVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AiToolConverter {

    AiToolConverter INSTANCE = Mappers.getMapper(AiToolConverter.class);

    AiToolVo entityVo(AiToolEntity entity);

    AiToolEntity formEntity(AiToolSaveRequest form);
}
