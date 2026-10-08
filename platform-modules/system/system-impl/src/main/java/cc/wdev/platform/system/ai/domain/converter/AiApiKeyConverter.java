package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiApiKeyEntity;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeyVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AiApiKeyConverter {

    AiApiKeyConverter INSTANCE = Mappers.getMapper(AiApiKeyConverter.class);

    AiApiKeyVo entity2Vo(AiApiKeyEntity entity);

}
