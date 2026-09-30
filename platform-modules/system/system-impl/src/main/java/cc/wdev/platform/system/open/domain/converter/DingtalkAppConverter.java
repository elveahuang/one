package cc.wdev.platform.system.open.domain.converter;

import cc.wdev.platform.system.open.domain.entity.DingtalkAppEntity;
import cc.wdev.platform.system.open.domain.form.DingtalkConfigForm;
import cc.wdev.platform.system.open.domain.vo.DingtalkAppVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 钉钉应用转换器
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DingtalkAppConverter {

    DingtalkAppConverter INSTANCE = Mappers.getMapper(DingtalkAppConverter.class);

    /**
     * 实体转VO
     */
    DingtalkAppVo entity2Vo(DingtalkAppEntity entity);

    /**
     * 表单转实体
     */
    DingtalkAppEntity form2Entity(DingtalkConfigForm form);

    /**
     * 实体转表单
     */
    DingtalkConfigForm entity2Form(DingtalkAppEntity entity);

}
