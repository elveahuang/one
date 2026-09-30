package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiKbTaskEntity;
import cc.wdev.platform.system.ai.domain.vo.AiKbTaskVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AiKbTaskConverter {

    AiKbTaskConverter INSTANCE = Mappers.getMapper(AiKbTaskConverter.class);

    AiKbTaskVo entity2Vo(AiKbTaskEntity entity);

}
