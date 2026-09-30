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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
     * @see AiSessionService#findByUserId(String)
     */
    @Override
    public List<AiSessionEntity> findByUserId(String userId) {
        if (ObjectUtils.isEmpty(userId)) {
            return List.of();
        }

        // 按创建日期和对话标识倒序排序
        return this.lambdaQueryWrapper()
            .eq(AiSessionEntity::getUserId, userId)
            .eq(AiSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(List.of(AiSessionEntity::getCreatedAt, AiSessionEntity::getSessionId))
            .list();
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
    public int deleteBySessionId(String sessionId) {
        return this.getMapper().delete(Wrappers.<AiSessionEntity>lambdaQuery()
            .eq(AiSessionEntity::getSessionId, sessionId)
        );
    }

    /**
     * @see AiSessionService#deleteExpiredSessions(LocalDateTime)
     */
    @Override
    public int deleteExpiredSessions(LocalDateTime before) {
        return this.getMapper().delete(Wrappers.<AiSessionEntity>lambdaQuery()
            .lt(AiSessionEntity::getExpiresAt, before)
        );
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
     * @see AiSessionService#findPageByUserId(String, Long, Pageable)
     */
    @Override
    public Page<AiSessionEntity> findPageByUserId(String userId, Long tenantId, Pageable pageable) {
        IPage<AiSessionEntity> page = this.lambdaQueryWrapper()
            .eq(StringUtils.isNotEmpty(userId), AiSessionEntity::getUserId, userId)
            .eq(ObjectUtils.isValidId(tenantId), AiSessionEntity::getTenantId, tenantId)
            .eq(AiSessionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(AiSessionEntity::getCreatedAt)
            .page(MyBatisPlusUtils.getMyBatisPlusPage(pageable));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see AiSessionService#incrementEventVersion(String)
     */
    @Override
    public int incrementEventVersion(String sessionId) {
        if (StringUtils.isEmpty(sessionId)) {
            return 0;
        }

        return this.getMapper().update(this.lambdaUpdate()
            .eq(AiSessionEntity::getSessionId, sessionId)
            .setSql("event_version = event_version + 1")
        );
    }

    /**
     * @see AiSessionService#decrementEventVersion(String)
     */
    @Override
    public int decrementEventVersion(String sessionId) {
        if (StringUtils.isEmpty(sessionId)) {
            return 0;
        }

        return this.getMapper().update(this.lambdaUpdate()
            .eq(AiSessionEntity::getSessionId, sessionId)
            .setSql("event_version = event_version - 1")
        );
    }

    /**
     * @see AiSessionService#casIncrementEventVersion(String, long)
     */
    @Override
    public int casIncrementEventVersion(String sessionId, long expectedVersion) {
        if (StringUtils.isEmpty(sessionId)) {
            return 0;
        }

        return this.getMapper().update(this.lambdaUpdate()
            .eq(AiSessionEntity::getSessionId, sessionId)
            .eq(AiSessionEntity::getEventVersion, expectedVersion)
            .set(AiSessionEntity::getEventVersion, expectedVersion + 1)
        );
    }

}
