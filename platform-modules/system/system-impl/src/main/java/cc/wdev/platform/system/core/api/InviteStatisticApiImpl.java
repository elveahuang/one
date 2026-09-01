package cc.wdev.platform.system.core.api;

import cc.wdev.platform.commons.utils.Base62Utils;
import cc.wdev.platform.system.core.domain.converter.InviteStatisticConverter;
import cc.wdev.platform.system.core.domain.entity.InviteStatisticEntity;
import cc.wdev.platform.system.core.domain.request.InviteStatisticRequest;
import cc.wdev.platform.system.core.domain.vo.InviteStatisticVo;
import cc.wdev.platform.system.core.service.InviteStatisticService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class InviteStatisticApiImpl implements InviteStatisticApi {

    private final InviteStatisticService inviteStatisticService;

    @Override
    public String generateInviteCode(InviteStatisticRequest request) {
        InviteStatisticEntity entity = inviteStatisticService.findStatistic(request);
        return Base62Utils.encode(entity.getId());
    }

    @Override
    public void initStatistic(InviteStatisticRequest request) {
        inviteStatisticService.initStatistic(request);
    }

    @Override
    public void deleteStatistic(InviteStatisticRequest request) {
        inviteStatisticService.deleteStatistic(request);
    }

    @Override
    public InviteStatisticVo getByCode(String inviteCode) {
        InviteStatisticEntity entity = inviteStatisticService.getByCode(inviteCode);
        return InviteStatisticConverter.INSTANCE.entity2Vo(entity);
    }

    @Override
    public InviteStatisticVo getInviteStatistic(InviteStatisticRequest request) {
        InviteStatisticEntity entity = inviteStatisticService.findStatistic(request);
        return InviteStatisticConverter.INSTANCE.entity2Vo(entity);
    }
}
