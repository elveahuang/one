package cc.wdev.platform.system.im.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.im.domain.converter.ChatSessionConverter;
import cc.wdev.platform.system.im.domain.entity.ChatSessionEntity;
import cc.wdev.platform.system.im.domain.request.ChatRequest;
import cc.wdev.platform.system.im.domain.request.ChatSaveRequest;
import cc.wdev.platform.system.im.domain.request.ChatSearchRequest;
import cc.wdev.platform.system.im.domain.request.ChatSessionRequest;
import cc.wdev.platform.system.im.domain.vo.ChatSessionVo;
import cc.wdev.platform.system.im.repository.ChatSessionRepository;
import cc.wdev.platform.system.im.service.ChatSessionService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.CHAT_SESSION;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class ChatSessionServiceImpl extends BaseCachingEntityService<ChatSessionEntity, Long, ChatSessionRepository>
    implements ChatSessionService {

    private final SimpleTenantCacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(CHAT_SESSION);

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see ChatSessionService#getChatSession(ChatRequest)
     */
    public ChatSessionVo getChatSession(ChatRequest request) {
        String bizTypeCode = StringUtils.nvl(request.getBizType()).trim();
        if (ObjectUtils.isValidId(request.getId())) {
            return ChatSessionConverter.INSTANCE.entityToVo(this.findById(request.getId()));
        }
        LambdaQueryChainWrapper<ChatSessionEntity> wrapper = this.lambdaQueryWrapper()
            .eq(ChatSessionEntity::getBizType, bizTypeCode)
            .eq(!ObjectUtils.isEmpty(request.getId()), ChatSessionEntity::getId, request.getId())
            .eq(!ObjectUtils.isEmpty(request.getBizId()), ChatSessionEntity::getBizId, request.getBizId())
            .eq(!ObjectUtils.isEmpty(request.getUserId()), ChatSessionEntity::getUserId, request.getUserId())
            .eq(ChatSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue());
        ChatSessionEntity entity = this.findOneByWrapper(wrapper);
        return ChatSessionConverter.INSTANCE.entityToVo(entity);
    }

    /**
     * @see ChatSessionService#list(ChatSearchRequest)
     */
    @Override
    public List<ChatSessionEntity> list(ChatSearchRequest request) {
        return this.lambdaQueryWrapper()
            .eq(ChatSessionEntity::getBizType, request.getBizType())
            .in(ChatSessionEntity::getBizId, request.getBizIds())
            .in(CollectionUtils.isNotEmpty(request.getUserIds()), ChatSessionEntity::getUserId, request.getUserIds())
            .list();
    }

    /**
     * @see ChatSessionService#saveChatSession(ChatRequest)
     */
    @Override
    @Transactional
    public ChatSessionVo saveChatSession(ChatRequest request) {
        ChatSessionEntity entity = null;
        if (!ObjectUtils.isEmpty(request.getId())) {
            entity = this.findById(request.getId());
        }
        if (ObjectUtils.isEmpty(entity)) {
            entity = ChatSessionEntity.builder()
                .bizType(request.getBizType())
                .bizId(request.getBizId())
                .userId(request.getUserId())
                .targetUserId(request.getTargetUserId())
                .build();
        } else {
            entity.setBizId(request.getBizId());
            entity.setTargetUserId(request.getTargetUserId());
        }
        // 保存会话实体
        entity = this.save(entity);
        return ChatSessionConverter.INSTANCE.entityToVo(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveBatchChatSession(List<ChatSaveRequest> requests) {
        List<ChatSessionEntity> exists = this.findCacheByIds(requests.stream().map(ChatSaveRequest::getId).toList(), (sids) -> {
            return this.lambdaQueryWrapper().in(ChatSessionEntity::getId, sids).list();
        });
        Map<Long, ChatSessionEntity> existsMap = exists.stream().collect(
            Collectors.toMap(ChatSessionEntity::getId, Function.identity(), (e, _) -> e));
        List<ChatSessionEntity> createList = Lists.newArrayList();
        List<ChatSessionEntity> updateList = Lists.newArrayList();
        LocalDateTime now = LocalDateTime.now();
        for (ChatSaveRequest request : requests) {
            ChatSessionEntity entity = existsMap.get(request.getId());
            if (ObjectUtils.isValidId(entity)) {
                ChatSessionEntity update = new ChatSessionEntity();
                ChatSessionConverter.INSTANCE.saveReq2Entity(request, update);
                update.setUpdatedAt(now);
                updateList.add(update);
                continue;
            }
            ChatSessionEntity create = new ChatSessionEntity();
            ChatSessionConverter.INSTANCE.saveReq2Entity(request, create);
            createList.add(create);
        }
        if (CollectionUtils.isNotEmpty(createList)) {
            this.insertBatch(createList);
            log.info("ChatSession insert success. size={}", createList.size());
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
            log.info("ChatSession update success. size={}", updateList.size());
        }
    }

    @Override
    public Map<Long, ChatSessionVo> chatSessionMap(Collection<Long> sessionIds) {
        if (CollectionUtils.isEmpty(sessionIds)) {
            return Collections.emptyMap();
        }
        List<ChatSessionEntity> entities = this.findCacheByIds(sessionIds);
        return entities.stream()
            .collect(Collectors.toMap(ChatSessionEntity::getId, ChatSessionConverter.INSTANCE::entityToVo));
    }

    @Override
    public List<ChatSessionEntity> getChatSessions(ChatSessionRequest request) {
        if (StringUtils.isBlank(request.getBizType()) || !ObjectUtils.isValidId(request.getUserId())) {
            return Collections.emptyList();
        }
        return lambdaQueryWrapper()
            .eq(ChatSessionEntity::getBizType, request.getBizType())
            .eq(ChatSessionEntity::getUserId, request.getUserId())
            .list();
    }

    @Override
    public long getChatSessionCount(ChatSessionRequest request) {
        return lambdaQueryWrapper()
            .eq(ChatSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(ChatSessionEntity::getBizType, request.getBizType())
            .eq(ChatSessionEntity::getTargetUserId, SecurityUtils.getUid())
            .ge(!ObjectUtils.isEmpty(request.getStartTime()), ChatSessionEntity::getUpdatedAt, request.getStartTime())
            .le(!ObjectUtils.isEmpty(request.getEndTime()), ChatSessionEntity::getUpdatedAt, request.getEndTime())
            .count();
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(ChatSessionEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(getCacheKeyGenerator().byId(model.getId()), model);
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(ChatSessionEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(getCacheKeyGenerator().byId(model.getId()));
            }
        }
    }

}
