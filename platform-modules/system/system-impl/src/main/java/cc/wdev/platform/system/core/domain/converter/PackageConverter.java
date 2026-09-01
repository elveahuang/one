package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.system.core.domain.entity.PackageEntity;
import cc.wdev.platform.system.core.domain.form.AddTenantPackageForm;
import cc.wdev.platform.system.core.domain.form.EditTenantPackageForm;
import cc.wdev.platform.system.core.domain.vo.TenantPackageVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageConverter {

    PackageConverter INSTANCE = Mappers.getMapper(PackageConverter.class);

    @Mapping(target = "cover", ignore = true)
    @Mapping(target = "source", ignore = true)
    TenantPackageVo entityToTenantPackageVo(PackageEntity entity);

    @Mapping(target = "bizType", ignore = true)
    @Mapping(target = "source", ignore = true)
    PackageEntity addFormToEntity(AddTenantPackageForm form);

    @Mapping(target = "bizType", ignore = true)
    @Mapping(target = "source", ignore = true)
    PackageEntity editFormToEntity(EditTenantPackageForm form);
}
