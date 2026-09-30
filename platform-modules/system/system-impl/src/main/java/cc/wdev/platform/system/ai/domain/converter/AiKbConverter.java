package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiKbEntity;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AiKbConverter {

    AiKbConverter INSTANCE = Mappers.getMapper(AiKbConverter.class);

    @Mapping(target = "embeddingModelId", ignore = true)
    @Mapping(target = "embeddingModel", ignore = true)
    @Mapping(target = "chatModelId", ignore = true)
    @Mapping(target = "chatModel", ignore = true)
    @Mapping(target = "rerankModelId", ignore = true)
    @Mapping(target = "rerankModel", ignore = true)
    @Mapping(target = "retrievalConfig", ignore = true)
    @Mapping(target = "chunkSize", ignore = true)
    @Mapping(target = "chunkOverlap", ignore = true)
    AiKbVo entity2Vo(AiKbEntity entity);

}
