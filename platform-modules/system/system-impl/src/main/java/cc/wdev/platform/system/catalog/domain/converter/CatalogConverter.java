package cc.wdev.platform.system.catalog.domain.converter;

import cc.wdev.platform.system.catalog.domain.entity.CatalogEntity;
import cc.wdev.platform.system.catalog.domain.request.CatalogSaveRequest;
import cc.wdev.platform.system.catalog.domain.vo.CatalogVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CatalogConverter {

    CatalogConverter INSTANCE = Mappers.getMapper(CatalogConverter.class);

    CatalogEntity saveDto2Entity(CatalogSaveRequest dto);

    CatalogVo entity2Vo(CatalogEntity entity);

}
