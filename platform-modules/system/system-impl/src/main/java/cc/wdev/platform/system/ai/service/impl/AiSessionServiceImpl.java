package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import cc.wdev.platform.system.ai.repository.AiSessionRepository;
import cc.wdev.platform.system.ai.service.AiSessionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiSessionServiceImpl
    extends BaseCachingEntityService<AiSessionEntity, Long, AiSessionRepository>
    implements AiSessionService {

    /**
     * @see AiSessionService#findBySessionId(String)
     */
    @Override
    public AiSessionEntity findBySessionId(String sessionId) {
        return this.findOneByWrapper(lambdaQueryWrapper().eq(AiSessionEntity::getSessionId, sessionId));
    }

    /**
     * @see AiSessionService#findBySessionIdAndUser(String, Long, Long)
     */
    @Override
    public AiSessionEntity findBySessionIdAndUser(String sessionId, Long userId, Long tenantId) {
        if (StringUtils.isEmpty(sessionId)) {
            return null;
        }
        return this.findOneByWrapper(lambdaQueryWrapper()
            .eq(AiSessionEntity::getSessionId, sessionId)
            .eq(AiSessionEntity::getUserId, String.valueOf(userId))
            .eq(ObjectUtils.isValidId(tenantId), AiSessionEntity::getTenantId, tenantId)
            .eq(AiSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue()));
    }

    /**
     * @see AiSessionService#deleteBySessionId(String)
     */
    @Override
    public void deleteBySessionId(String sessionId) {
        this.lambdaUpdateWrapper().eq(AiSessionEntity::getSessionId, sessionId).remove();
    }

    @Override
    public List<AiSessionEntity> findByUserId(String userId) {
        if (ObjectUtils.isEmpty(userId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiSessionEntity::getUserId, userId)
            .eq(AiSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    /**
     * @see AiSessionService#findByUserId(String, Long)
     */
    @Override
    public List<AiSessionEntity> findByUserId(String userId, Long tenantId) {
        if (ObjectUtils.isEmpty(userId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiSessionEntity::getUserId, userId)
            .eq(ObjectUtils.isValidId(tenantId), AiSessionEntity::getTenantId, tenantId)
            .eq(AiSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(AiSessionEntity::getCreatedAt)
            .list(MyBatisPlusUtils.getLimitPage(1000));
    }

    /**
     * @see AiSessionService#findByUserIdPage(String, Long, Pageable)
     */
    @Override
    public Page<AiSessionEntity> findByUserIdPage(String userId, Long tenantId, Pageable pageable) {
        IPage<AiSessionEntity> page = this.lambdaQueryWrapper()
            .eq(StringUtils.isNotEmpty(userId), AiSessionEntity::getUserId, userId)
            .eq(ObjectUtils.isValidId(tenantId), AiSessionEntity::getTenantId, tenantId)
            .eq(AiSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(AiSessionEntity::getCreatedAt)
            .page(MyBatisPlusUtils.getMyBatisPlusPage(pageable));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    @Override
    public List<AiSessionEntity> findExpiredSessions(LocalDateTime now) {
        if (ObjectUtils.isEmpty(now)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .isNotNull(AiSessionEntity::getExpiresAt)
            .le(AiSessionEntity::getExpiresAt, now)
            .eq(AiSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    @Override
    public int incrementEventVersionIfMatch(Long id, long expectedVersion) {
        if (id == null) {
            return 0;
        }
        return this.lambdaUpdateWrapper()
            .eq(AiSessionEntity::getId, id)
            .eq(AiSessionEntity::getEventVersion, expectedVersion)
            .setSql("event_version = event_version + 1")
            .update() ? 1 : 0;
    }
}
