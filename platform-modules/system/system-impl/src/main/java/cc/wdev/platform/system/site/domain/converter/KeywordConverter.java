package cc.wdev.platform.system.site.domain.converter;

import cc.wdev.platform.system.site.domain.entity.KeywordEntity;
import cc.wdev.platform.system.site.domain.form.KeywordForm;
import cc.wdev.platform.system.site.domain.vo.KeywordVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface KeywordConverter {
    KeywordConverter INSTANCE = Mappers.getMapper(KeywordConverter.class);

    KeywordEntity formToEntity(KeywordForm form);

    KeywordVo entityToVo(KeywordEntity entity);
}
