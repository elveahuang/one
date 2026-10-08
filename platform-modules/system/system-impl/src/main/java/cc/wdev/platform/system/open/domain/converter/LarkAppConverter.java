package cc.wdev.platform.system.open.domain.converter;

import cc.wdev.platform.system.open.domain.entity.LarkAppEntity;
import cc.wdev.platform.system.open.domain.form.LarkConfigForm;
import cc.wdev.platform.system.open.domain.vo.LarkAppVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 飞书应用转换器
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LarkAppConverter {

    LarkAppConverter INSTANCE = Mappers.getMapper(LarkAppConverter.class);

    /**
     * 实体转VO
     */
    LarkAppVo entity2Vo(LarkAppEntity entity);

    /**
     * 表单转实体
     */
    LarkAppEntity form2Entity(LarkConfigForm form);

    /**
     * 实体转表单
     */
    LarkConfigForm entity2Form(LarkAppEntity entity);

}
