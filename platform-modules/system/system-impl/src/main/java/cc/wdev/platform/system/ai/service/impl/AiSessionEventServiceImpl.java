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
import org.jspecify.annotations.NonNull;
import org.springframework.ai.session.EventFilter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

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

        return this.lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AiSessionEventEntity::getArchived, ActiveTypeEnum.DISABLED.getValue())
            .orderByAsc(AiSessionEventEntity::getId)
            .list();
    }

    /**
     * @see AiSessionEventService#archiveByIds(String, List)
     */
    @Override
    public void archiveByIds(String sessionId, List<String> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return;
        }

        long expected = ids.stream().distinct().count();
        Long actual = this.lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .in(AiSessionEventEntity::getSessionEventId, ids)
            .count();
        if (actual == null || actual != expected) {
            throw new IllegalArgumentException(
                "archiveIds contains an event that is not in the log of session " + sessionId);
        }

        this.lambdaUpdateWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .in(AiSessionEventEntity::getSessionEventId, ids)
            .set(AiSessionEventEntity::getArchived, ActiveTypeEnum.ENABLED.getValue())
            .update();
    }

    /**
     * @see AiSessionEventService#findBySessionEventId(String)
     */
    @Override
    public AiSessionEventEntity findBySessionEventId(String sessionEventId) {
        if (ObjectUtils.isEmpty(sessionEventId)) {
            return null;
        }

        return this.findOneByWrapper(lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionEventId, sessionEventId));
    }

    /**
     * @see AiSessionEventService#findFromSequence(String, Long)
     */
    @Override
    public List<AiSessionEventEntity> findFromSequence(String sessionId, Long fromSequence) {
        if (ObjectUtils.isEmpty(sessionId) || fromSequence == null) {
            return List.of();
        }

        return this.lambdaQueryWrapper()
            .in(AiSessionEventEntity::getSessionId, sessionId)
            .ge(AiSessionEventEntity::getId, fromSequence)
            .orderByAsc(AiSessionEventEntity::getId)
            .list();
    }

    /**
     * @see AiSessionEventService#deleteFromSequence(String, Long)
     */
    @Override
    public boolean deleteFromSequence(String sessionId, Long fromSequence) {
        if (ObjectUtils.isEmpty(sessionId) || fromSequence == null) {
            return false;
        }
        return this.lambdaUpdateWrapper()
            .in(AiSessionEventEntity::getSessionId, sessionId)
            .ge(AiSessionEventEntity::getId, fromSequence)
            .remove();
    }

    /**
     * @see AiSessionEventService#findTurnHead(String, String, EventFilter)
     */
    @Override
    public List<AiSessionEventEntity> findTurnHead(String sessionId, String firstEventId, EventFilter filter) {
        if (ObjectUtils.isEmpty(sessionId) || ObjectUtils.isEmpty(firstEventId) || filter == null) {
            return List.of();
        }
        LocalDateTime from = filter.from() != null ? LocalDateTime.ofInstant(filter.from(), ZoneOffset.UTC) : null;
        LocalDateTime to = filter.to() != null ? LocalDateTime.ofInstant(filter.to(), ZoneOffset.UTC) : null;
        List<String> messageTypes = filter.messageTypes() == null ? null
            : filter.messageTypes().stream().map(Enum::name).toList();
        return this.getMapper().findTurnHead(sessionId, firstEventId, from, to, messageTypes,
            filter.excludeSynthetic(), filter.excludeArchived());
    }

    @Override
    public List<AiSessionEventEntity> findEvents(@NonNull String sessionId, @NonNull EventFilter filter) {
        LambdaQueryChainWrapper<AiSessionEventEntity> wrapper = this.lambdaQueryWrapper();
        wrapper.eq(AiSessionEventEntity::getSessionId, sessionId);
        wrapper.eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue());
        if (filter.excludeArchived()) {
            wrapper.eq(AiSessionEventEntity::getArchived, ActiveTypeEnum.DISABLED.getValue());
        }
        if (filter.from() != null) {
            wrapper.ge(AiSessionEventEntity::getTimestamp, LocalDateTime.ofInstant(filter.from(), ZoneOffset.UTC));
        }
        if (filter.to() != null) {
            wrapper.le(AiSessionEventEntity::getTimestamp, LocalDateTime.ofInstant(filter.to(), ZoneOffset.UTC));
        }
        if (filter.messageTypes() != null && !filter.messageTypes().isEmpty()) {
            wrapper.in(AiSessionEventEntity::getMessageType, filter.messageTypes().stream().map(Enum::name).collect(Collectors.toList()));
        }
        if (filter.excludeSynthetic()) {
            wrapper.eq(AiSessionEventEntity::getSynthetic, ActiveTypeEnum.DISABLED.getValue());
        }
        if (filter.keyword() != null) {
            wrapper.apply("LOWER(COALESCE(message_content, '')) LIKE {0} ESCAPE '!'", containsPattern(filter.keyword()));
        }
        if (filter.lastN() != null) {
            wrapper.orderByDesc(AiSessionEventEntity::getId).last("LIMIT " + filter.lastN());
        } else if (filter.pageSize() != null) {
            int page = filter.page() != null ? filter.page() : 0;
            wrapper.orderByAsc(AiSessionEventEntity::getId).last("LIMIT " + filter.pageSize() + " OFFSET " + ((long) page * filter.pageSize()));
        } else {
            wrapper.orderByAsc(AiSessionEventEntity::getId);
        }
        return wrapper.list();
    }

    /**
     * Turns a search term into a literal LIKE pattern, escaping the SQL wildcard characters.
     */
    private static String containsPattern(String term) {
        String escaped = term.replace("!", "!!").replace("%", "!%").replace("_", "!_");
        return "%" + escaped + "%";
    }

    @Override
    public Page<AiSessionEventVo> findHistory(AiSessionEventRequest request) {
        List<String> sessionIds = findHistorySessionIds(request);
        if (sessionIds != null && sessionIds.isEmpty()) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }

        LambdaQueryChainWrapper<AiSessionEventEntity> wrapper = this.lambdaQueryWrapper();
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
