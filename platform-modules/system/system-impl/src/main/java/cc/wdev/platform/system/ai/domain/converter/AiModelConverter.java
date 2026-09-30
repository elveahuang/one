package cc.wdev.platform.system.ai.domain.converter;

import cc.wdev.platform.commons.ai.utils.AiSecretUtils;
import cc.wdev.platform.system.ai.domain.entity.AiModelEntity;
import cc.wdev.platform.system.ai.domain.request.AiModelSaveRequest;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AiModelConverter {

    AiModelConverter INSTANCE = Mappers.getMapper(AiModelConverter.class);

    AiModelEntity form2Entity(AiModelSaveRequest form);

    AiModelVo entityVo(AiModelEntity entity);

    /**
     * 读取时解密 API Key（内部调用使用，JSON 输出由 @SensitiveMark 脱敏）
     */
    @AfterMapping
    default void decryptApiKey(AiModelEntity entity, @MappingTarget AiModelVo vo) {
        if (vo.getApiKey() != null) {
            vo.setApiKey(AiSecretUtils.decrypt(vo.getApiKey()));
        }
    }
}
