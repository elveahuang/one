package cc.wdev.platform.system.message.service.impl;

import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.message.domain.entity.MessageTypeEntity;
import cc.wdev.platform.system.message.repository.MessageTypeRepository;
import cc.wdev.platform.system.message.request.MessageTypeSearchRequest;
import cc.wdev.platform.system.message.service.MessageTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.MESSAGE_TYPE;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@Service
public class MessageTypeServiceImpl
    extends BaseCachingEntityService<MessageTypeEntity, Long, MessageTypeRepository>
    implements MessageTypeService {

    private final SimpleCacheKeyGenerator cacheKeyGenerator = new SimpleCacheKeyGenerator(MESSAGE_TYPE);

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public SimpleCacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see MessageTypeService#search(MessageTypeSearchRequest)
     */
    @Override
    public List<MessageTypeEntity> search(MessageTypeSearchRequest request) {
        return this.lambdaQueryWrapper()
            .in(CollectionUtils.isNotEmpty(request.getCodeList()), MessageTypeEntity::getCode, request.getCodeList())
            .eq(MessageTypeEntity::getStatus, StatusTypeEnum.ON.getValue())
            .eq(MessageTypeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

}
