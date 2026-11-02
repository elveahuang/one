package cc.wdev.platform.system.im;

import cc.wdev.platform.commons.message.model.SimpleJsonMessage;
import cc.wdev.platform.system.im.enums.ChatRespMessageTypeEnum;
import cc.wdev.platform.system.im.message.ChatRespMessage;
import cc.wdev.platform.system.im.utils.ImUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.List;

/**
 * IM WebSocket 推送载荷单元测试。
 *
 * <p>锁住两个易回归点：</p>
 * <ol>
 *   <li>回执/消息载荷必须可序列化且包在 {@code ChatRespMessage{type,data}} 里（前端按 content.type 分发）；</li>
 *   <li>接收者使用**用户ID**（不是 sessionId），否则点对点推送会错投或广播。</li>
 * </ol>
 *
 * @author elvea
 */
class ImChatPushPayloadTests {

    private record ChatReadReceipt(Long chatSessionId, Long readerUserId, Long lastReadMessageId) implements Serializable {
    }

    private record SampleVo(Long id, String text) implements Serializable {
    }

    @Test
    void testMessagePayloadCarriesTypeAndData() {
        SampleVo vo = new SampleVo(1L, "hello");
        SimpleJsonMessage<ChatRespMessage<SampleVo>> message = ImUtils.createJsonMessage(
            List.of(100L), ImUtils.createChatRespMessage(ChatRespMessageTypeEnum.MESSAGE.getValue(), vo));

        Assertions.assertEquals("json", message.getType());
        Assertions.assertEquals(List.of(100L), message.getReceivers());
        Assertions.assertNotNull(message.getContent());
        Assertions.assertEquals("MESSAGE", message.getContent().getType());
    }

    /**
     * 已读回执载荷必须是 Serializable（WebSocket 会序列化），且 type 用 READ
     */
    @Test
    void testReadReceiptPayloadIsSerializable() {
        ChatReadReceipt receipt = new ChatReadReceipt(10L, 200L, 99L);

        Assertions.assertInstanceOf(Serializable.class, receipt);

        SimpleJsonMessage<ChatRespMessage<ChatReadReceipt>> message = ImUtils.createJsonMessage(
            List.of(100L), ImUtils.createChatRespMessage(ChatRespMessageTypeEnum.READ.getValue(), receipt));
        Assertions.assertEquals("json", message.getType());
        Assertions.assertEquals("READ", message.getContent().getType());
    }

    /**
     * 三个推送类型值稳定：前端按字符串分发，改名会静默失效
     */
    @Test
    void testRespMessageTypeValuesAreStable() {
        Assertions.assertEquals("MESSAGE", ChatRespMessageTypeEnum.MESSAGE.getValue());
        Assertions.assertEquals("READ", ChatRespMessageTypeEnum.READ.getValue());
        Assertions.assertEquals("SESSION", ChatRespMessageTypeEnum.SESSION.getValue());
    }

    /**
     * 没传 receivers 时保持空集合语义（ServletWebSocketManager 据此决定是否广播）
     */
    @Test
    void testEmptyReceiversRemainEmpty() {
        SimpleJsonMessage<ChatRespMessage<SampleVo>> message = ImUtils.createJsonMessage(
            List.of(), ImUtils.createChatRespMessage(ChatRespMessageTypeEnum.SESSION.getValue(), new SampleVo(2L, "x")));

        Assertions.assertTrue(message.getReceivers() == null || message.getReceivers().isEmpty());
    }

}
