package cc.wdev.platform.system.region.domain.convert;

import cc.wdev.platform.system.region.domain.entity.RegionEntity;
import cc.wdev.platform.system.region.domain.vo.RegionVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 地区转换器
 *
 * @author erden
 */
@Mapper
public interface RegionConverter {

    RegionConverter INSTANCE = Mappers.getMapper(RegionConverter.class);

    /**
     * Entity 转 VO
     */
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    RegionVo entity2Vo(RegionEntity entity);
}
