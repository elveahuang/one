package cc.wdev.platform.system.region.domain.convert;

import cc.wdev.platform.system.region.domain.entity.AddressEntity;
import cc.wdev.platform.system.region.domain.form.AddressForm;
import cc.wdev.platform.system.region.domain.vo.AddressVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressConverter {

    AddressConverter INSTANCE = Mappers.getMapper(AddressConverter.class);


    @Mapping(target = "bizId", ignore = true)
    @Mapping(target = "extra", ignore = true)
    @Mapping(target = "status", ignore = true)
    AddressEntity form2Entity(AddressForm form);

    @Mapping(target = "countryName", ignore = true)
    @Mapping(target = "provinceName", ignore = true)
    @Mapping(target = "cityName", ignore = true)
    @Mapping(target = "countyName", ignore = true)
    @Mapping(target = "distance", ignore = true)
    AddressVo entity2Vo(AddressEntity entity);

}
