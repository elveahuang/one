package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信回调消息类型
 */
@Getter
@AllArgsConstructor
public enum WeiXinMsgTypeEnum implements BaseEnum<String> {
    NONE("none", "未知"),
    TEXT("text", "文本"),
    EVENT("event", "事件"),
    ;
    /**
     * 类型
     */
    private final String value;
    /**
     * 描述
     */
    private final String description;
}
