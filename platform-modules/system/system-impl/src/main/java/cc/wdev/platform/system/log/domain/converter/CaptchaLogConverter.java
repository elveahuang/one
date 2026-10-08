package cc.wdev.platform.system.log.domain.converter;

import cc.wdev.platform.commons.extensions.captcha.domain.CaptchaLogDto;
import cc.wdev.platform.system.log.domain.entity.CaptchaLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface CaptchaLogConverter {

    CaptchaLogConverter INSTANCE = Mappers.getMapper(CaptchaLogConverter.class);

    CaptchaLogEntity dto2Entity(CaptchaLogDto dto);

}
