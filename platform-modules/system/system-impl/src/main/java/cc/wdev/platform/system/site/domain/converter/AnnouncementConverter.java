package cc.wdev.platform.system.site.domain.converter;

import cc.wdev.platform.system.site.domain.entity.AnnouncementEntity;
import cc.wdev.platform.system.site.domain.form.AnnouncementForm;
import cc.wdev.platform.system.site.domain.vo.AnnouncementVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface AnnouncementConverter {

    AnnouncementConverter INSTANCE = Mappers.getMapper(AnnouncementConverter.class);

    AnnouncementEntity form2Entity(AnnouncementForm form);

    AnnouncementVo entity2Vo(AnnouncementEntity announcementEntity);
}
