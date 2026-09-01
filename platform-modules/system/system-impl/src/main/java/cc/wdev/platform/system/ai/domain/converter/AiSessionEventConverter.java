package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AiSessionEventConverter {

    AiSessionEventConverter INSTANCE = Mappers.getMapper(AiSessionEventConverter.class);

    @Mapping(target = "tenantId", ignore = true)
    AiSessionEventVo entity2Vo(AiSessionEventEntity entity);

}
