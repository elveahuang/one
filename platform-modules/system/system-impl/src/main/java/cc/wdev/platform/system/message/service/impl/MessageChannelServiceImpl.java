package cc.wdev.platform.system.message.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.message.domain.entity.MessageChannelEntity;
import cc.wdev.platform.system.message.repository.MessageChannelRepository;
import cc.wdev.platform.system.message.request.MessageChannelSearchRequest;
import cc.wdev.platform.system.message.service.MessageChannelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.MESSAGE_CHANNEL;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@Service
public class MessageChannelServiceImpl
    extends BaseCachingEntityService<MessageChannelEntity, Long, MessageChannelRepository>
    implements MessageChannelService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleCacheKeyGenerator(MESSAGE_CHANNEL);

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see MessageChannelService#search(MessageChannelSearchRequest)
     */
    @Override
    public List<MessageChannelEntity> search(MessageChannelSearchRequest request) {
        return this.lambdaQueryWrapper()
            .in(CollectionUtils.isNotEmpty(request.getCodeList()), MessageChannelEntity::getCode, request.getCodeList())
            .eq(ObjectUtils.isNotEmpty(request.getStatus()), MessageChannelEntity::getStatus, request.getStatus())
            .eq(MessageChannelEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

}
