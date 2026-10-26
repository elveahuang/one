package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.ai.domain.converter.AiSessionEventConverter;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import cc.wdev.platform.system.ai.repository.AiSessionEventRepository;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import cc.wdev.platform.system.ai.service.AiSessionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.session.EventType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiSessionEventServiceImpl
    extends BaseCachingEntityService<AiSessionEventEntity, Long, AiSessionEventRepository>
    implements AiSessionEventService {

    private final AiSessionService aiSessionService;

    /**
     * @see AiSessionEventService#deleteBySessionId(String)
     */
    @Override
    public void deleteBySessionId(String sessionId) {
        this.lambdaUpdateWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .remove();
    }

    /**
     * @see AiSessionEventService#findBySessionId(String)
     */
    @Override
    public List<AiSessionEventEntity> findBySessionId(String sessionId) {
        if (ObjectUtils.isEmpty(sessionId)) {
            return List.of();
        }

        // 新 SessionRepository 契约要求读取完整日志，activeView 由 CompactionBoundary 投影。
        return this.lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiSessionEventEntity::getId)
            .list();
    }

    /**
     * @see AiSessionEventService#findBySessionEventId(String)
     */
    @Override
    public AiSessionEventEntity findBySessionEventId(String sessionEventId) {
        if (ObjectUtils.isEmpty(sessionEventId)) {
            return null;
        }

        return this.findOne(lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionEventId, sessionEventId));
    }

    /**
     * @see AiSessionEventService#findBySessionEventIds(List)
     */
    @Override
    public List<AiSessionEventEntity> findBySessionEventIds(List<String> sessionEventIds) {
        if (ObjectUtils.isEmpty(sessionEventIds)) {
            return List.of();
        }

        return this.lambdaQueryWrapper()
            .in(AiSessionEventEntity::getSessionEventId, sessionEventIds)
            .list();
    }

    @Override
    public Page<AiSessionEventVo> findHistory(AiSessionEventRequest request) {
        List<String> sessionIds = findHistorySessionIds(request);
        if (sessionIds != null && sessionIds.isEmpty()) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }

        LambdaQueryChainWrapper<AiSessionEventEntity> wrapper = this.lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getEventType, EventType.MESSAGE.name())
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue());
        if (sessionIds != null) {
            wrapper.in(AiSessionEventEntity::getSessionId, sessionIds);
        }
        IPage<AiSessionEventEntity> entityPage = wrapper
            .orderByDesc(AiSessionEventEntity::getCreatedAt)
            .orderByDesc(AiSessionEventEntity::getId)
            .page(getMyBatisPlusPage(request));
        if (!MyBatisPlusUtils.isNotEmpty(entityPage)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<AiSessionEventVo> eventVos = entityPage.getRecords().stream()
            .map(AiSessionEventConverter.INSTANCE::entity2Vo)
            .toList();
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), eventVos, entityPage.getTotal());
    }

    private List<String> findHistorySessionIds(AiSessionEventRequest request) {
        if (request.getAiSessionId() != null) {
            AiSessionEntity session = this.aiSessionService.findById(request.getAiSessionId());
            if (session == null || request.getUserId() != null
                && !String.valueOf(request.getUserId()).equals(session.getUserId())) {
                return List.of();
            }
            return List.of(session.getSessionId());
        }
        if (request.getUserId() != null) {
            return this.aiSessionService.findByUserId(String.valueOf(request.getUserId())).stream()
                .map(AiSessionEntity::getSessionId)
                .toList();
        }
        return null;
    }

    @Override
    public Page<AiSessionEventVo> findCurrent(AiSessionEventRequest request) {
        IPage<AiSessionEventEntity> entityPage =
            lambdaQueryWrapper()
                .eq(AiSessionEventEntity::getSessionId, request.getSessionId())
                .eq(AiSessionEventEntity::getEventType, EventType.MESSAGE.name())
                .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .orderByDesc(AiSessionEventEntity::getCreatedAt)
                .page(getMyBatisPlusPage(request));
        List<AiSessionEventEntity> records = entityPage.getRecords();
        if (!MyBatisPlusUtils.isNotEmpty(entityPage)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<AiSessionEventVo> eventVos = records.stream().map(AiSessionEventConverter.INSTANCE::entity2Vo).toList();
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), eventVos, entityPage.getTotal());
    }

}
