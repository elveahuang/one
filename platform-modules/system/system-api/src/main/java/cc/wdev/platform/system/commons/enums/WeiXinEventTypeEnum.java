package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 微信回调事件类型
 */
@Getter
@AllArgsConstructor
public enum WeiXinEventTypeEnum implements BaseEnum<String> {
    NONE("none", "未知"),
    SUBSCRIBE("subscribe", "订阅"),
    UNSUBSCRIBE("unsubscribe", "取消订阅"),
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
