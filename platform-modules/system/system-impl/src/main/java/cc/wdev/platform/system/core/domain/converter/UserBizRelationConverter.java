package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.commons.utils.MapStructUtils;
import cc.wdev.platform.system.core.domain.entity.UserBizRelationEntity;
import cc.wdev.platform.system.core.domain.request.UserBizRelationReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserBizRelationConverter {

    UserBizRelationConverter INSTANCE = MapStructUtils.getConverter(UserBizRelationConverter.class);

    @Mapping(target = "bizId", ignore = true)
    UserBizRelationEntity saveReq2Entity(UserBizRelationReq req);
}
