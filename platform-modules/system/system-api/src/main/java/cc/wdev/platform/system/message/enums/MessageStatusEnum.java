package cc.wdev.platform.system.message.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elvea
 */
@Getter
@RequiredArgsConstructor
public enum MessageStatusEnum implements BaseEnum<Integer> {
    PENDING(1, "PENDING", "等待发送"),
    SENT(2, "SENT", "已发送"),
    SENDING(3, "SENDING", "发送中"),
    FAIL(4, "FAIL", "发送失败"),
    ;

    private final Integer value;
    private final String code;
    private final String description;
}
