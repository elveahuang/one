package cc.wdev.platform.system.im.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 聊天消息状态枚举
 */
@Getter
@AllArgsConstructor
public enum ChatMessageStatusEnum implements BaseEnum<Integer> {
    UNSENT(0, "SENDING", "未发送"),
    SENT(1, "SENT", "已发送"),
    READ(2, "READ", "已读"),
    PENDING(3, "PENDING", "待发送"),
    SENDING(4, "SENDING", "发送中"),
    FAIL(5, "FAIL", "发送失败");

    private final Integer value;
    private final String code;
    private final String description;
}
