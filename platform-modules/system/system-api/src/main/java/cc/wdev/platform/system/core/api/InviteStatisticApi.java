package cc.wdev.platform.system.core.api;

import cc.wdev.platform.system.core.domain.request.InviteStatisticRequest;
import cc.wdev.platform.system.core.domain.vo.InviteStatisticVo;

public interface InviteStatisticApi {

    String generateInviteCode(InviteStatisticRequest request);

    void initStatistic(InviteStatisticRequest request);

    void deleteStatistic(InviteStatisticRequest request);

    InviteStatisticVo getByCode(String inviteCode);

    InviteStatisticVo getInviteStatistic(InviteStatisticRequest request);
}
