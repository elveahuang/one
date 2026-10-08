package cc.wdev.platform.system.message.domain.converter;

import cc.wdev.platform.system.message.domain.entity.MessageTemplateEntity;
import cc.wdev.platform.system.message.domain.vo.MessageTemplateVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface MessageTemplateConverter {

    MessageTemplateConverter INSTANCE = Mappers.getMapper(MessageTemplateConverter.class);

    MessageTemplateEntity formToEntity(MessageTemplateVo vo);

    MessageTemplateVo entityToVo(MessageTemplateEntity vo);

}
