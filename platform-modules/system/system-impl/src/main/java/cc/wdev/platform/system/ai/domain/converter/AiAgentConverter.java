package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiAgentEntity;
import cc.wdev.platform.system.ai.domain.request.AiAgentSaveRequest;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AiAgentConverter {

    AiAgentConverter INSTANCE = Mappers.getMapper(AiAgentConverter.class);

    @Mapping(target = "rolePrompt", ignore = true)
    @Mapping(target = "temperature", ignore = true)
    @Mapping(target = "prompt", ignore = true)
    AiAgentEntity form2Entity(AiAgentSaveRequest form);

    @Mapping(target = "modelId", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "kbId", ignore = true)
    @Mapping(target = "kb", ignore = true)
    @Mapping(target = "toolIds", ignore = true)
    @Mapping(target = "toolNames", ignore = true)
    @Mapping(target = "prompt", ignore = true)
    @Mapping(target = "mcpServerIds", ignore = true)
    AiAgentVo entity2Vo(AiAgentEntity entity);

}
