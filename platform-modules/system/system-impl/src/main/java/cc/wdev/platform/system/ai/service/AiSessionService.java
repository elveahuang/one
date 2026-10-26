package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author elvea
 */
public interface AiSessionService extends CachingEntityService<AiSessionEntity, Long> {

    /**
     * 根据会话ID查询会话
     */
    AiSessionEntity findBySessionId(String sessionId);

    /**
     * 根据会话ID批量查询会话
     */
    List<AiSessionEntity> findBySessionIds(Collection<String> sessionIds);

    /**
     * 根据用户ID查询会话
     */
    List<AiSessionEntity> findByUserId(String userId);

    AiSessionEntity findBySessionIdAndUser(String sessionId, Long userId, Long tenantId);

    /**
     * 删除指定的会话
     */
    int deleteBySessionId(String sessionId);

    /**
     * 查询已过期会话。
     */
    List<AiSessionEntity> findExpiredSessions(LocalDateTime before);

    /**
     * 按过期条件删除单个会话。
     */
    int deleteExpiredSession(String sessionId, LocalDateTime before);

    /**
     * 根据用户ID + 租户查询
     */
    List<AiSessionEntity> findByUserId(String userId, Long tenantId);

    /**
     * 分页查询用户会话（租户隔离）
     */
    Page<AiSessionEntity> findPageByUserId(String userId, Long tenantId, Pageable pageable);

    int incrementEventVersion(String sessionId);

    int decrementEventVersion(String sessionId);

    int casIncrementEventVersion(String sessionId, long expectedVersion);

}
