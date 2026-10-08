package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiKbChunkEntity;
import cc.wdev.platform.system.ai.domain.vo.AiKbChunkVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AiKbChunkConverter {

    AiKbChunkConverter INSTANCE = Mappers.getMapper(AiKbChunkConverter.class);

    AiKbChunkVo entity2Vo(AiKbChunkEntity entity);

}
