package cc.wdev.platform.system.open.domain.converter;

import cc.wdev.platform.system.open.domain.entity.WxMpAppEntity;
import cc.wdev.platform.system.open.domain.form.WxMpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMpAppVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WxMpAppConverter {

    WxMpAppConverter INSTANCE = Mappers.getMapper(WxMpAppConverter.class);

    WxMpAppVo entity2Vo(WxMpAppEntity entity);

    WxMpAppEntity Form2Entity(WxMpConfigForm form);

    WxMpConfigForm entity2Form(WxMpAppEntity entity);

}
