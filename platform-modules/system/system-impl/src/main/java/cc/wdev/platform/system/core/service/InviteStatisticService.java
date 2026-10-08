package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.core.domain.entity.InviteStatisticEntity;
import cc.wdev.platform.system.core.domain.request.InviteStatisticRequest;

public interface InviteStatisticService extends CachingEntityService<InviteStatisticEntity, Long> {
    /**
     * 根据用户类型查询统计数据
     */
    InviteStatisticEntity findStatistic(InviteStatisticRequest request);

    /**
     * 根据邀请码查询统计数据
     */
    InviteStatisticEntity getByCode(String inviteCode);

    /**
     * 初始用户统计数据
     */
    InviteStatisticEntity initStatistic(InviteStatisticRequest request);

    /**
     * 删除统计数据
     */
    void deleteStatistic(InviteStatisticRequest request);
}
