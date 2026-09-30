package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.utils.Base62Utils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.core.domain.entity.InviteStatisticEntity;
import cc.wdev.platform.system.core.domain.request.InviteStatisticRequest;
import cc.wdev.platform.system.core.repository.InviteStatisticRepository;
import cc.wdev.platform.system.core.service.InviteStatisticService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.INVITE_STATISTIC;

@Service
public class InviteStatisticServiceImpl extends BaseCachingEntityService<InviteStatisticEntity, Long, InviteStatisticRepository> implements InviteStatisticService {

    private final SimpleTenantCacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(INVITE_STATISTIC);

    /**
     * @see CachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    @Override
    public InviteStatisticEntity findStatistic(InviteStatisticRequest request) {
        Long userId = request.getUserId();
        if (!ObjectUtils.isValidId(userId)) {
            return null;
        }

        InviteStatisticEntity entity = this.findByCacheKey(this.cacheKeyGenerator.byId(userId), _ ->
            this.findOneByWrapper(lambdaQueryWrapper().eq(InviteStatisticEntity::getUserId, userId)));
        if (!ObjectUtils.isValidId(entity)) {
            return this.initStatistic(request);
        }
        return entity;
    }

    @Override
    public InviteStatisticEntity getByCode(String inviteCode) {
        if (StringUtils.isBlank(inviteCode)) {
            return null;
        }
        return this.findByCacheKey(this.cacheKeyGenerator.byCode(inviteCode), _ -> {
            Long inviteId = Base62Utils.decode(inviteCode);
            return this.findById(inviteId);
        });
    }

    @Override
    public InviteStatisticEntity initStatistic(InviteStatisticRequest request) {
        Long entityId = request.getUserId();
        if (!ObjectUtils.isValidId(entityId)) {
            return null;
        }

        InviteStatisticEntity entity = this.findOneByWrapper(lambdaQueryWrapper()
            .eq(InviteStatisticEntity::getUserId, entityId));
        if (ObjectUtils.isValidId(entity)) {
            entity.setActive(ActiveTypeEnum.ENABLED.getValue());
            this.save(entity);
            return entity;
        }

        entity = InviteStatisticEntity.builder()
            .userId(entityId)
            .build();
        this.save(entity);
        return entity;
    }

    @Override
    public void deleteStatistic(InviteStatisticRequest request) {
        List<Long> entityIds = Lists.newArrayList();
        if (ObjectUtils.isValidId(request.getUserId())) {
            entityIds.add(request.getUserId());
        }
        if (CollectionUtils.isNotEmpty(request.getUserIds())) {
            entityIds.addAll(request.getUserIds());
        }
        if (CollectionUtils.isEmpty(entityIds)) {
            return;
        }

        this.softDeleteBatch(this.findListByWrapper(lambdaQueryWrapper()
            .eq(InviteStatisticEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .in(InviteStatisticEntity::getUserId, entityIds)));
    }

}
