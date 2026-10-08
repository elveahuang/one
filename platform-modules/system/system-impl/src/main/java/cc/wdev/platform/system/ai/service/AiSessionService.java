package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
     * 根据用户ID查询会话
     */
    List<AiSessionEntity> findByUserId(String userId);

    AiSessionEntity findBySessionIdAndUser(String sessionId, Long userId, Long tenantId);

    /**
     * 删除指定的会话
     */
    int deleteBySessionId(String sessionId);

    /**
     * 原子删除已过期会话，事件由外键级联删除。
     */
    int deleteExpiredSessions(LocalDateTime before);

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
