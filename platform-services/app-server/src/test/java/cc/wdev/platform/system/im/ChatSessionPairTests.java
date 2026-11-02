package cc.wdev.platform.system.im;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * IM 会话「对端解析」语义单元测试。
 *
 * <p>背景：会话只存一条记录（收发共用）。曾一度改为「双向建会话」（A→B、B→A 各一条），
 * 结果消息按 chat_session_id 归属，导致一次对话被拆成两条互不相通的线程 ——
 * B 看不到 A 的消息、A 收不到 B 的回复，未读数与最后一条消息全部失效。
 * 这里锁住「单会话 + 对端解析」的正确语义，防止再走回头路。</p>
 *
 * @author elvea
 */
class ChatSessionPairTests {

    /**
     * 与 ImChatApiImpl#resolveOppositeUserId 保持一致的解析规则
     */
    private static Long resolveOppositeUserId(Long sessionUserId, Long sessionTargetUserId, Long currentUserId) {
        if (java.util.Objects.equals(sessionUserId, currentUserId)) {
            return sessionTargetUserId;
        }
        if (java.util.Objects.equals(sessionTargetUserId, currentUserId)) {
            return sessionUserId;
        }
        return null;
    }

    @Test
    void testSenderSeesReceiverAsOpposite() {
        // 会话：A(1) -> B(2)
        Assertions.assertEquals(2L, resolveOppositeUserId(1L, 2L, 1L));
    }

    /**
     * 反向视角：B 打开同一条会话，对端应是 A —— 这正是双向建会话时丢掉的能力
     */
    @Test
    void testReceiverSeesSenderAsOpposite() {
        Assertions.assertEquals(1L, resolveOppositeUserId(1L, 2L, 2L));
    }

    /**
     * 会话外的人不能解析出对端（用于拒绝越权发送）
     */
    @Test
    void testOutsiderHasNoOpposite() {
        Assertions.assertNull(resolveOppositeUserId(1L, 2L, 999L));
    }

    /**
     * 同一对用户的两个方向必须映射到同一会话键（LEAST/GREATEST 归一）
     */
    @Test
    void testPairKeyIsDirectionIndependent() {
        Assertions.assertEquals(pairKey(1L, 2L), pairKey(2L, 1L));
        Assertions.assertNotEquals(pairKey(1L, 2L), pairKey(1L, 3L));
    }

    private static String pairKey(Long a, Long b) {
        return Math.min(a, b) + "|" + Math.max(a, b);
    }

}
