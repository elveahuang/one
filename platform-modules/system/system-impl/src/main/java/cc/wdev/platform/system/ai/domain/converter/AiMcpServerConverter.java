package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.system.ai.domain.entity.AiMcpServerEntity;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSaveRequest;
import cc.wdev.platform.system.ai.domain.vo.AiMcpServerVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * MCP Server 转换器。
 *
 * <p>{@code unmappedTargetPolicy = ERROR} 是刻意的：该实体此前新增字段时转换器静默漏映射，
 * 导致 API 保存后字段永远为空（如 command/env）。改为编译期报错，避免再次出现「只改了一半」。</p>
 *
 * @author elvea
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AiMcpServerConverter {
    AiMcpServerConverter INSTANCE = Mappers.getMapper(AiMcpServerConverter.class);

    AiMcpServerEntity form2Entity(AiMcpServerSaveRequest form);

    AiMcpServerVo entityVo(AiMcpServerEntity entity);
}
