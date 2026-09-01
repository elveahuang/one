package cc.wdev.platform.system.site.domain.converter;

import cc.wdev.platform.system.site.domain.entity.LinkEntity;
import cc.wdev.platform.system.site.domain.form.LinkForm;
import cc.wdev.platform.system.site.domain.vo.LinkVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface LinkConverter {

    LinkConverter INSTANCE = Mappers.getMapper(LinkConverter.class);

    LinkEntity formToEntity(LinkForm form);

    LinkVo entityToVo(LinkEntity entity);
}
