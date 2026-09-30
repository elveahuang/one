package cc.wdev.platform.system.im.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 聊天会话类型
 * 1. 私聊
 * 2. 群聊
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum ChatSessionBizTypeEnum implements BaseEnum<String> {
    IM_DIRECT_CHAT("IM_DIRECT_CHAT", "直接聊天"),
    IM_GROUP_CHAT("IM_GROUP_CHAT", "群聊");

    private final String value;
    private final String description;
}
