package cc.wdev.platform.system.site.domain.converter;

import cc.wdev.platform.system.site.domain.entity.BannerEntity;
import cc.wdev.platform.system.site.domain.form.BannerForm;
import cc.wdev.platform.system.site.domain.vo.BannerVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface BannerConverter {

    BannerConverter INSTANCE = Mappers.getMapper(BannerConverter.class);

    BannerEntity formToEntity(BannerForm form);

    BannerVo entityToVo(BannerEntity entity);
}
