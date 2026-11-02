package cc.wdev.platform.system.ai.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 知识库向量化任务重试退避策略单元测试
 *
 * <p>覆盖 2026-11 修复：原实现虽声明 nextRetryAt，但实体无 retryCount 字段、且退避参数被忽略，
 * 导致任务永远重试或永远不重试。这里锁定指数退避与上限语义。</p>
 *
 * @author elvea
 */
class AiVectorRetryBackoffTests {

    /**
     * 首次失败退避 5 分钟，之后 10、20 分钟
     */
    @Test
    void testExponentialBackoff() {
        Assertions.assertEquals(5L, AiVectorServiceImpl.resolveBackoffMinutes(0));
        Assertions.assertEquals(10L, AiVectorServiceImpl.resolveBackoffMinutes(1));
        Assertions.assertEquals(20L, AiVectorServiceImpl.resolveBackoffMinutes(2));
    }

    /**
     * 达到上限后返回 -1，调用方应据此标记 FAILED 而不是继续排队重试
     */
    @Test
    void testRetryExhausted() {
        Assertions.assertEquals(-1L, AiVectorServiceImpl.resolveBackoffMinutes(3));
        Assertions.assertEquals(-1L, AiVectorServiceImpl.resolveBackoffMinutes(4));
        Assertions.assertEquals(-1L, AiVectorServiceImpl.resolveBackoffMinutes(100));
    }

    /**
     * 脏数据（负的重试次数）不应触发无限重试
     */
    @Test
    void testNegativeRetryCountIsTreatedAsExhausted() {
        Assertions.assertEquals(-1L, AiVectorServiceImpl.resolveBackoffMinutes(-1));
    }

    /**
     * 退避时间必须严格递增，确保不会出现「重试越快越频繁」的雪崩
     */
    @Test
    void testBackoffIsMonotonic() {
        long previous = 0L;
        for (int i = 0; i < 3; i++) {
            long minutes = AiVectorServiceImpl.resolveBackoffMinutes(i);
            Assertions.assertTrue(minutes > previous, "退避时间未递增，retryCount=" + i);
            previous = minutes;
        }
    }

}
