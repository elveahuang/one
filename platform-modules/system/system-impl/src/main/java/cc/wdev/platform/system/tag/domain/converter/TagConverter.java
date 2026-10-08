package cc.wdev.platform.system.tag.domain.converter;

import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.tag.domain.entity.TagEntity;
import cc.wdev.platform.system.tag.domain.request.TagSaveRequest;
import cc.wdev.platform.system.tag.domain.vo.TagVo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author irving
 */
@Mapper
public interface TagConverter {

    TagConverter INSTANCE = Mappers.getMapper(TagConverter.class);

    @Mapping(target = "extra", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "referenceId", ignore = true)
    @Mapping(target = "bizId", ignore = true)
    TagEntity formToEntity(TagSaveRequest form);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "extra", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "bizId", ignore = true)
    @Mapping(target = "referenceId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromForm(TagSaveRequest form, @MappingTarget TagEntity entity);

    @AfterMapping
    default void formToEntity(TagSaveRequest form, @MappingTarget TagEntity entity) {
        if (CollectionUtils.isNotEmpty(form.getBizIdList())) {
            entity.setBizId(form.getBizIdList().getFirst());
        }
        // tenantId 在父类中，Builder需要手动映射
        if (form.getTenantId() != null) {
            entity.setTenantId(form.getTenantId());
        }
    }

    TagVo entity2Vo(TagEntity entity);

}
