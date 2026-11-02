package cc.wdev.platform.system.im.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * IM WebSocket 推送消息类型
 *
 * <p>前端 `store/socket.ts` 收到 `newMessage` 事件后按 `content.type` 分发业务类型。</p>
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum ChatRespMessageTypeEnum implements BaseEnum<String> {
    MESSAGE("MESSAGE", "新消息"),
    READ("READ", "已读回执"),
    SESSION("SESSION", "会话变更");

    private final String value;
    private final String description;
}
