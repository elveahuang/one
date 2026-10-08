package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.system.core.domain.dto.TenantDto;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.domain.form.AddTenantForm;
import cc.wdev.platform.system.core.domain.vo.TenantVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author erden
 */
@Mapper
public interface TenantConverter {

    TenantConverter INSTANCE = Mappers.getMapper(TenantConverter.class);

    /**
     * 实体转DTO
     */
    TenantDto entity2Dto(TenantEntity entity);

    @Mapping(target = "cover", ignore = true)
    @Mapping(target = "packageIds", ignore = true)
    TenantVo entity2Vo(TenantEntity entity);

    @Mapping(target = "accountCount", ignore = true)
    @Mapping(target = "rootInd", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "expirationDate", ignore = true)
    TenantEntity addFormToEntity(AddTenantForm form);

}
