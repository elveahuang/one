package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiKbItemEntity;
import cc.wdev.platform.system.ai.domain.vo.AiKbItemVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AiKbItemConverter {

    AiKbItemConverter INSTANCE = Mappers.getMapper(AiKbItemConverter.class);

    AiKbItemVo entity2Vo(AiKbItemEntity entity);

}
