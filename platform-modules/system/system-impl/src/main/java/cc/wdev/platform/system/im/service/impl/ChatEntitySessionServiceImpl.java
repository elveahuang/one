package cc.wdev.platform.system.im.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.im.cache.ChatEntitySessionCacheKeyGenerator;
import cc.wdev.platform.system.im.domain.entity.ChatEntitySessionEntity;
import cc.wdev.platform.system.im.domain.request.ChatEntitySessionMarkRequest;
import cc.wdev.platform.system.im.domain.request.ChatEntitySessionRequest;
import cc.wdev.platform.system.im.domain.request.ChatEntitySessionSearchRequest;
import cc.wdev.platform.system.im.repository.ChatEntitySessionRepository;
import cc.wdev.platform.system.im.service.ChatEntitySessionService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatEntitySessionServiceImpl extends BaseCachingEntityService<ChatEntitySessionEntity, Long, ChatEntitySessionRepository> implements ChatEntitySessionService {

    private final ChatEntitySessionCacheKeyGenerator cacheKeyGenerator = new ChatEntitySessionCacheKeyGenerator();

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see ChatEntitySessionService#markChatEntitySession(ChatEntitySessionMarkRequest)
     */
    @Override
    public ChatEntitySessionEntity getChatEntitySession(ChatEntitySessionRequest request) {
        ChatEntitySessionEntity entity = this.findChatEntitySessionCache(request);
        if (entity == null) {
            entity = ChatEntitySessionEntity.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(request.getUserId())
                .topInd(BooleanTypeEnum.getFalseValue())
                .clearInd(BooleanTypeEnum.getFalseValue())
                .clearAt(LocalDateTime.MIN)
                .lastReadMessageId(0L)
                .build();
        }
        return entity;
    }

    @Override
    public List<ChatEntitySessionEntity> getChatEntitySessions(ChatEntitySessionSearchRequest request) {
        if (CollectionUtils.isNotEmpty(request.getIds())) {
            return this.findCacheByIds(request.getIds());
        }
        return this.lambdaQueryWrapper()
            .in(ChatEntitySessionEntity::getChatSessionId, request.getChatSessionIds())
            .in(ChatEntitySessionEntity::getUserId, request.getUserIds())
            .list();
    }

    /**
     * @see ChatEntitySessionService#markChatEntitySession(ChatEntitySessionMarkRequest)
     */
    @Override
    public ChatEntitySessionEntity markChatEntitySession(ChatEntitySessionMarkRequest request) {
        ChatEntitySessionEntity entity = this.findChatEntitySessionCache(ChatEntitySessionRequest.builder()
            .chatSessionId(request.getChatSessionId())
            .userId(request.getUserId())
            .build());
        if (entity == null) {
            entity = ChatEntitySessionEntity.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(request.getUserId())
                .lastReadMessageId(0L)
                .build();
        }
        if (request.getChatMessageId() > entity.getLastReadMessageId()) {
            entity.setLastReadMessageId(request.getChatMessageId());
            entity.setLastReadAt(LocalDateTime.now());
        }
        this.save(entity);
        return entity;
    }

    @Override
    public void doTop(ChatEntitySessionRequest request) {
        LocalDateTime now = LocalDateTime.now();
        ChatEntitySessionEntity entity = this.findChatEntitySessionCache(ChatEntitySessionRequest.builder()
            .chatSessionId(request.getChatSessionId())
            .userId(request.getUserId())
            .build());

        if (!ObjectUtils.isValidId(entity)) {
            entity = ChatEntitySessionEntity.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(request.getUserId())
                .topAt(now)
                .topInd(BooleanTypeEnum.getTrueValue())
                .build();
            this.save(entity);
        }

        entity.setTopInd(BooleanTypeEnum.getTrueValue());
        entity.setTopAt(now);
        this.save(entity);
        return;
    }

    @Override
    public void undoTop(ChatEntitySessionRequest request) {
        ChatEntitySessionEntity entity = this.findChatEntitySessionCache(ChatEntitySessionRequest.builder()
            .chatSessionId(request.getChatSessionId())
            .userId(request.getUserId())
            .build());
        if (!ObjectUtils.isValidId(entity.getId())) {
            throw new ServiceException(ResponseCodeEnum.NOT_PRESENT);
        }

        this.deleteCache(entity);
        this.lambdaUpdateWrapper()
            .eq(ChatEntitySessionEntity::getId, entity.getId())
            .set(ChatEntitySessionEntity::getTopInd, BooleanTypeEnum.getFalseValue())
            .set(ChatEntitySessionEntity::getTopAt, null)
            .update();
    }

    @Override
    public void batchUndoTop(List<ChatEntitySessionRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }
        List<Long> ids = Lists.newArrayListWithCapacity(requests.size());
        for (ChatEntitySessionRequest request : requests) {
            ChatEntitySessionEntity entity = this.findChatEntitySessionCache(ChatEntitySessionRequest.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(request.getUserId())
                .build());
            if (!ObjectUtils.isValidId(entity)) {
                continue;
            }
            this.deleteCache(entity);
            ids.add(entity.getId());
        }
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        this.lambdaUpdateWrapper()
            .in(ChatEntitySessionEntity::getId, ids)
            .set(ChatEntitySessionEntity::getTopInd, BooleanTypeEnum.getFalseValue())
            .set(ChatEntitySessionEntity::getTopAt, null)
            .update();
    }

    @Override
    public ChatEntitySessionEntity doCollect(ChatEntitySessionRequest request) {
        LocalDateTime now = LocalDateTime.now();
        ChatEntitySessionEntity entity = this.findChatEntitySessionCache(ChatEntitySessionRequest.builder()
            .chatSessionId(request.getChatSessionId())
            .userId(request.getUserId())
            .build());

        if (!ObjectUtils.isValidId(entity)) {
            entity = ChatEntitySessionEntity.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(request.getUserId())
                .collectAt(now)
                .collectInd(BooleanTypeEnum.getTrueValue())
                .build();
            this.save(entity);
            return entity;
        }
        entity.setCollectInd(BooleanTypeEnum.getTrueValue());
        entity.setCollectAt(now);
        this.save(entity);
        return entity;
    }

    @Override
    public ChatEntitySessionEntity undoCollect(ChatEntitySessionRequest request) {
        ChatEntitySessionEntity entity = this.findChatEntitySessionCache(ChatEntitySessionRequest.builder()
            .chatSessionId(request.getChatSessionId())
            .userId(request.getUserId())
            .build());
        if (!ObjectUtils.isValidId(entity.getId())) {
            throw new ServiceException(ResponseCodeEnum.NOT_PRESENT);
        }

        this.deleteCache(entity);
        this.lambdaUpdateWrapper()
            .eq(ChatEntitySessionEntity::getId, entity.getId())
            .set(ChatEntitySessionEntity::getCollectInd, BooleanTypeEnum.getFalseValue())
            .set(ChatEntitySessionEntity::getCollectAt, null)
            .update();
        entity.setCollectInd(BooleanTypeEnum.getFalseValue());
        entity.setCollectAt(null);
        return entity;
    }

    @Override
    public List<ChatEntitySessionEntity> batchUndoCollect(List<ChatEntitySessionRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return Collections.emptyList();
        }
        List<Long> ids = Lists.newArrayListWithCapacity(requests.size());
        List<ChatEntitySessionEntity> entities = Lists.newArrayListWithCapacity(requests.size());
        for (ChatEntitySessionRequest request : requests) {
            ChatEntitySessionEntity entity = this.findChatEntitySessionCache(ChatEntitySessionRequest.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(request.getUserId())
                .build());
            if (!ObjectUtils.isValidId(entity)) {
                continue;
            }
            this.deleteCache(entity);
            entity.setCollectInd(BooleanTypeEnum.getFalseValue());
            entity.setCollectAt(null);
            entities.add(entity);
            ids.add(entity.getId());
        }
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        this.lambdaUpdateWrapper()
            .in(ChatEntitySessionEntity::getId, ids)
            .set(ChatEntitySessionEntity::getCollectInd, BooleanTypeEnum.getFalseValue())
            .set(ChatEntitySessionEntity::getCollectAt, null)
            .update();
        return entities;
    }

    @Override
    public void clear(ChatEntitySessionRequest request) {
        LocalDateTime now = LocalDateTime.now();

        ChatEntitySessionEntity entity = this.findChatEntitySessionCache(ChatEntitySessionRequest.builder()
            .chatSessionId(request.getChatSessionId())
            .userId(request.getUserId())
            .build());

        if (null == entity) {
            entity = ChatEntitySessionEntity.builder()
                .chatSessionId(request.getChatSessionId())
                .userId(request.getUserId())
                .clearAt(now)
                .clearInd(BooleanTypeEnum.getTrueValue())
                .build();
            super.save(entity);
            return;
        }

        this.deleteCache(entity);
        this.lambdaUpdateWrapper()
            .eq(ChatEntitySessionEntity::getId, entity.getId())
            .set(ChatEntitySessionEntity::getClearInd, BooleanTypeEnum.getTrueValue())
            .set(ChatEntitySessionEntity::getClearAt, now)
            .set(ChatEntitySessionEntity::getTopInd, BooleanTypeEnum.getFalseValue())
            .set(ChatEntitySessionEntity::getTopAt, null)
            .set(ChatEntitySessionEntity::getCollectInd, BooleanTypeEnum.getFalseValue())
            .set(ChatEntitySessionEntity::getCollectAt, null)
            .update();
    }

    @Override
    public void reopen(Long sid, Collection<Long> userIds) {
        if (!ObjectUtils.isValidId(sid) || CollectionUtils.isEmpty(userIds)) {
            return;
        }

        List<ChatEntitySessionEntity> updateList = this.findCacheByBizIds(userIds,
                (entityId) -> cacheKeyGenerator.byEntity(sid, entityId),
                (bizIds) -> this.lambdaQueryWrapper()
                    .eq(ChatEntitySessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                    .eq(ChatEntitySessionEntity::getChatSessionId, sid)
                    .in(ChatEntitySessionEntity::getUserId, bizIds)
                    .list())
            .stream()
            .filter(entity -> BooleanTypeEnum.TRUE.getValue().equals(entity.getClearInd()))
            .toList();
        if (CollectionUtils.isEmpty(updateList)) {
            return;
        }

        // 清除缓存
        List<CacheKey> cacheKeys = updateList.stream()
            .map(entity -> cacheKeyGenerator.byEntity(sid, entity.getUserId()))
            .collect(Collectors.toList());
        this.getCacheService().delete(cacheKeys);

        // 更新DB
        List<Long> updateIds = updateList.stream().map(ChatEntitySessionEntity::getId).collect(Collectors.toList());
        this.lambdaUpdateWrapper()
            .in(ChatEntitySessionEntity::getId, updateIds)
            .set(ChatEntitySessionEntity::getClearInd, BooleanTypeEnum.getFalseValue())
            .update();
    }

    @Override
    public Map<Long, Integer> chatSessionTopIndMap(Collection<Long> chatSessionIds, Long userId) {
        if (CollectionUtils.isEmpty(chatSessionIds)) {
            return Collections.emptyMap();
        }
        List<ChatEntitySessionEntity> entities = this.lambdaQueryWrapper()
            .select(ChatEntitySessionEntity::getChatSessionId, ChatEntitySessionEntity::getTopInd)
            .in(ChatEntitySessionEntity::getChatSessionId, chatSessionIds)
            .eq(ChatEntitySessionEntity::getUserId, userId)
            .list();
        return entities.stream().collect(
            Collectors.toMap(ChatEntitySessionEntity::getChatSessionId, ChatEntitySessionEntity::getTopInd));
    }

    @Override
    public Map<Long, ChatEntitySessionEntity> chatEntitySessionMap(Collection<Long> chatSessionIds, Long userId) {
        if (CollectionUtils.isEmpty(chatSessionIds)) {
            return Collections.emptyMap();
        }
        List<ChatEntitySessionEntity> entities = this.lambdaQueryWrapper()
            .in(ChatEntitySessionEntity::getChatSessionId, chatSessionIds)
            .eq(ChatEntitySessionEntity::getUserId, userId)
            .list();
        return entities.stream().collect(
            Collectors.toMap(ChatEntitySessionEntity::getChatSessionId, Function.identity(), (e, _) -> e));
    }

    private ChatEntitySessionEntity findChatEntitySessionCache(ChatEntitySessionRequest request) {
        CacheKey cacheKey = cacheKeyGenerator.byEntity(request.getChatSessionId(), request.getUserId());
        return this.findByCacheKey(cacheKey, _ -> {
            return this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(ChatEntitySessionEntity::getChatSessionId, request.getChatSessionId())
                .eq(ChatEntitySessionEntity::getUserId, request.getUserId()));
        });
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(ChatEntitySessionEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(cacheKeyGenerator.byId(model.getId()), model);
            }
            getCacheService().set(cacheKeyGenerator.byEntity(model.getChatSessionId(), model.getUserId()), model);
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(ChatEntitySessionEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(cacheKeyGenerator.byId(model.getId()));
            }
            getCacheService().delete(cacheKeyGenerator.byEntity(model.getChatSessionId(), model.getUserId()));
        }
    }

}
