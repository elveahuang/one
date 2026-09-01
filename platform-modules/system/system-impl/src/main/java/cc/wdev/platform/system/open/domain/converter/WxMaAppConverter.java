package cc.wdev.platform.system.open.domain.converter;

import cc.wdev.platform.system.open.domain.entity.WxMaAppEntity;
import cc.wdev.platform.system.open.domain.form.WxMaConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMaAppVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WxMaAppConverter {

    WxMaAppConverter INSTANCE = Mappers.getMapper(WxMaAppConverter.class);

    WxMaAppEntity Form2Entity(WxMaConfigForm form);

    WxMaAppVo Entity2Vo(WxMaAppEntity entity);

    WxMaConfigForm entity2Form(WxMaAppEntity entity);

}
