package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiMcpServerEntity;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSaveRequest;
import cc.wdev.platform.system.ai.domain.vo.AiMcpServerVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AiMcpServerConverter {
    AiMcpServerConverter INSTANCE = Mappers.getMapper(AiMcpServerConverter.class);

    AiMcpServerEntity form2Entity(AiMcpServerSaveRequest form);

    AiMcpServerVo entityVo(AiMcpServerEntity entity);
}
