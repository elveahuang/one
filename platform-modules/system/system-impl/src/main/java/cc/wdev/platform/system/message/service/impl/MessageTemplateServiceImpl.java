package cc.wdev.platform.system.message.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.message.domain.entity.MessageTemplateEntity;
import cc.wdev.platform.system.message.repository.MessageTemplateRepository;
import cc.wdev.platform.system.message.service.MessageTemplateService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.utils.ObjectUtils.isNotEmpty;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.MESSAGE_TEMPLATE;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@Service
public class MessageTemplateServiceImpl
    extends BaseCachingEntityService<MessageTemplateEntity, Long, MessageTemplateRepository>
    implements MessageTemplateService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(MESSAGE_TEMPLATE);

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see MessageTemplateService#getMessageTemplateEntity(String, String)
     */
    @Override
    public MessageTemplateEntity getMessageTemplateEntity(String messageType, String messageChannel) {
        LambdaQueryChainWrapper<MessageTemplateEntity> wrapper = lambdaQueryWrapper()
            .eq(MessageTemplateEntity::getMessageType, messageType)
            .eq(MessageTemplateEntity::getMessageChannel, messageChannel);
        return this.findOneByWrapper(wrapper);
    }

    /**
     * @see MessageTemplateService#findMessageTemplate(String, List)
     */
    @Override
    public List<MessageTemplateEntity> findMessageTemplate(String messageType, List<String> messageChannelCodes) {
        return lambdaQueryWrapper()
            .eq(MessageTemplateEntity::getMessageType, messageType)
            .in(isNotEmpty(messageChannelCodes), MessageTemplateEntity::getMessageChannel, messageChannelCodes)
            .list();
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(MessageTemplateEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(this.cacheKeyGenerator.byId(model.getId()), model);
            }
            if (!ObjectUtils.isEmpty(model.getMessageType()) && !ObjectUtils.isEmpty(model.getMessageChannel())) {
                getCacheService().set(this.cacheKeyGenerator.key(model.getMessageType(), model.getMessageChannel()), model);
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(MessageTemplateEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(this.cacheKeyGenerator.byId(model.getId()));
            }
            if (!ObjectUtils.isEmpty(model.getMessageType()) && !ObjectUtils.isEmpty(model.getMessageChannel())) {
                getCacheService().delete(this.cacheKeyGenerator.key(model.getMessageType(), model.getMessageChannel()));
            }
        }
    }

}
