package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.ai.domain.converter.AiSessionEventConverter;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEventEntity;
import cc.wdev.platform.system.ai.domain.request.AiSessionEventRequest;
import cc.wdev.platform.system.ai.domain.vo.AiSessionEventVo;
import cc.wdev.platform.system.ai.repository.AiSessionEventRepository;
import cc.wdev.platform.system.ai.service.AiSessionEventService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
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
public class AiSessionEventServiceImpl
    extends BaseCachingEntityService<AiSessionEventEntity, Long, AiSessionEventRepository>
    implements AiSessionEventService {

    @Override
    public void deleteBySessionId(String sessionId) {
        this.lambdaUpdateWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .remove();
    }

    @Override
    public List<AiSessionEventEntity> findBySessionId(String sessionId) {
        if (ObjectUtils.isEmpty(sessionId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiSessionEventEntity::getTimestamp)
            .list();
    }

    @Override
    public List<AiSessionEventEntity> findActiveBySessionId(String sessionId) {
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

    @Override
    public void archiveByIds(List<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return;
        }
        this.lambdaUpdateWrapper()
            .in(AiSessionEventEntity::getId, ids)
            .set(AiSessionEventEntity::getArchived, ActiveTypeEnum.ENABLED.getValue())
            .update();
    }

    @Override
    public void replaceActiveWindow(String sessionId, List<AiSessionEventEntity> retainedEvents) {
        if (ObjectUtils.isEmpty(sessionId)) {
            return;
        }
        // 软删除当前 active 窗口
        this.lambdaUpdateWrapper()
            .eq(AiSessionEventEntity::getSessionId, sessionId)
            .eq(AiSessionEventEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AiSessionEventEntity::getArchived, ActiveTypeEnum.DISABLED.getValue())
            .remove();
        // 重建 retained 窗口（清除主键，交由 insert 生成）
        if (ObjectUtils.isNotEmpty(retainedEvents)) {
            List<AiSessionEventEntity> toSave = retainedEvents.stream().peek(e -> e.setId(null)).collect(Collectors.toList());
            this.saveBatch(toSave);
        }
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
        if (filter.branch() != null) {
            wrapper.and(w -> w.isNull(AiSessionEventEntity::getBranch)
                .or().eq(AiSessionEventEntity::getBranch, filter.branch())
                .or().apply("{0} LIKE CONCAT(branch, '.%')", filter.branch()));
        }
        if (filter.keyword() != null) {
            wrapper.apply("LOWER(message_content) LIKE {0}", "%" + filter.keyword() + "%");
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

    @Override
    public Page<AiSessionEventVo> findHistory(AiSessionEventRequest request) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AiSessionEventVo> page =
            this.mapper.findHistory(getMyBatisPlusPage(request), request.getUserId(), request.getAiSessionId());
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), page.getRecords(), page.getTotal());
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
