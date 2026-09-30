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

    AiSessionEntity findBySessionId(String sessionId);

    /**
     * 根据会话ID + 用户 + 租户查询（归属校验）
     */
    AiSessionEntity findBySessionIdAndUser(String sessionId, Long userId, Long tenantId);

    void deleteBySessionId(String sessionId);

    /**
     * 根据用户ID查询会话
     */
    List<AiSessionEntity> findByUserId(String userId);

    /**
     * 根据用户ID + 租户查询
     */
    List<AiSessionEntity> findByUserId(String userId, Long tenantId);

    /**
     * 分页查询用户会话（租户隔离）
     */
    Page<AiSessionEntity> findByUserIdPage(String userId, Long tenantId, Pageable pageable);

    /**
     * 查询已过期的会话
     */
    List<AiSessionEntity> findExpiredSessions(LocalDateTime now);

    /**
     *
     */
    int incrementEventVersionIfMatch(Long id, long expectedVersion);

}
