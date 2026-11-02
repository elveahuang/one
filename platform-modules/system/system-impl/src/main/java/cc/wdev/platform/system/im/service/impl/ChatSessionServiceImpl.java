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
        ChatSessionEntity entity = this.findOne(wrapper);
        return ChatSessionConverter.INSTANCE.entityToVo(entity);
    }

    /**
     * @see ChatSessionService#list(ChatSearchRequest)
     */
    @Override
    public List<ChatSessionEntity> list(ChatSearchRequest request) {
        LambdaQueryChainWrapper<ChatSessionEntity> wrapper = this.lambdaQueryWrapper()
            .eq(ChatSessionEntity::getBizType, request.getBizType())
            .in(CollectionUtils.isNotEmpty(request.getBizIds()), ChatSessionEntity::getBizId, request.getBizIds())
            .eq(ChatSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue());
        // 会话只存一条（收发共用），因此「我的会话」= 我发起的 或 别人发给我的
        Collection<Long> userIds = request.getUserIds();
        if (CollectionUtils.isNotEmpty(userIds)) {
            wrapper.and(w -> w
                .in(ChatSessionEntity::getUserId, userIds)
                .or()
                .in(ChatSessionEntity::getTargetUserId, userIds));
        }
        return wrapper.list();
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
    public ChatSessionEntity findByPair(String bizType, Long bizId, Long userId, Long targetUserId) {
        // 不分收发方向：A->B 与 B->A 视为同一会话，避免一次对话被拆成两条互不相通的线程
        return this.lambdaQueryWrapper()
            .eq(ChatSessionEntity::getBizType, bizType)
            .eq(ChatSessionEntity::getBizId, bizId)
            .eq(ChatSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .and(w -> w
                .nested(n -> n.eq(ChatSessionEntity::getUserId, userId)
                    .eq(ChatSessionEntity::getTargetUserId, targetUserId))
                .or()
                .nested(n -> n.eq(ChatSessionEntity::getUserId, targetUserId)
                    .eq(ChatSessionEntity::getTargetUserId, userId)))
            .last("LIMIT 1")
            .one();
    }

    @Override
    public List<ChatSessionEntity> findByMember(String bizType, Collection<Long> bizIds, Long userId) {
        if (ObjectUtils.isInvalidId(userId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(ChatSessionEntity::getBizType, bizType)
            .in(CollectionUtils.isNotEmpty(bizIds), ChatSessionEntity::getBizId, bizIds)
            .eq(ChatSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            // 既包含自己发起的会话，也包含别人发给自己的会话
            .and(w -> w.eq(ChatSessionEntity::getUserId, userId)
                .or()
                .eq(ChatSessionEntity::getTargetUserId, userId))
            .list();
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
        // 会话只存一条（收发共用），「某用户的会话」= 他发起或别人发给他的
        return lambdaQueryWrapper()
            .eq(ChatSessionEntity::getBizType, request.getBizType())
            .and(w -> w.eq(ChatSessionEntity::getUserId, request.getUserId())
                .or()
                .eq(ChatSessionEntity::getTargetUserId, request.getUserId()))
            .list();
    }

    @Override
    public long getChatSessionCount(ChatSessionRequest request) {
        Long currentUserId = SecurityUtils.getUid();
        return lambdaQueryWrapper()
            .eq(ChatSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(ChatSessionEntity::getBizType, request.getBizType())
            // 会话只存一条，当前用户既可能是发起方也可能是接收方，只按 target_user_id 计会漏掉自己发起的
            .and(w -> w.eq(ChatSessionEntity::getUserId, currentUserId)
                .or()
                .eq(ChatSessionEntity::getTargetUserId, currentUserId))
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
