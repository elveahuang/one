package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.system.core.domain.entity.InviteStatisticEntity;
import cc.wdev.platform.system.core.domain.vo.InviteStatisticVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InviteStatisticConverter {

    InviteStatisticConverter INSTANCE = Mappers.getMapper(InviteStatisticConverter.class);

    InviteStatisticVo entity2Vo(InviteStatisticEntity inviteStatisticEntity);
}
