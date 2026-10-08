package cc.wdev.platform.system.open.domain.converter;

import cc.wdev.platform.system.open.domain.entity.WxCpAppEntity;
import cc.wdev.platform.system.open.domain.form.WxCpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxCpAppVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 企业微信应用转换器
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WxCpAppConverter {

    WxCpAppConverter INSTANCE = Mappers.getMapper(WxCpAppConverter.class);

    /**
     * 实体转VO
     */
    WxCpAppVo entity2Vo(WxCpAppEntity entity);

    /**
     * 表单转实体
     */
    WxCpAppEntity form2Entity(WxCpConfigForm form);

    /**
     * 实体转表单
     */
    WxCpConfigForm entity2Form(WxCpAppEntity entity);

}
