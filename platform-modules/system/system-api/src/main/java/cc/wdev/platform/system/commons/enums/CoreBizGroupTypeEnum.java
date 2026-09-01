package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 核心业务分组类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum CoreBizGroupTypeEnum implements BaseEnum<String> {
    CONFIG_TYPE("CONFIG_TYPE", "系统配置项类型"),
    ROLE_TYPE("ROLE_TYPE", "角色类型"),
    CONFIG_CONTENT_TYPE("CONFIG_CONTENT_TYPE", "系统配置项内容类型"),
    ATTACHMENT_TYPE("ATTACHMENT_TYPE", "附件业务类型"),
    CATALOG_TYPE("CATALOG_TYPE", "目录业务类型"),
    DICT_TYPE("DICT_TYPE", "字典业务类型"),
    TAG_TYPE("TAG_TYPE", "标签业务类型"),
    IM_CHAT_SESSION_TYPE("IM_CHAT_SESSION_TYPE", "互动业务类型"),
    IM_CHAT_SESSION_LABEL_TYPE("IM_CHAT_SESSION_LABEL_TYPE", "互动标签业务类型"),
    ORDER_TYPE("ORDER_TYPE", "订单业务类型"),
    PAY_TYPE("PAY_TYPE", "支付业务类型"),
    MESSAGE_CHANNEL_TYPE("MESSAGE_CHANNEL_TYPE", "消息通道类型"),
    MESSAGE_TYPE("MESSAGE_TYPE", "消息类型"),
    NONE("NONE", "NONE");

    private final String value;
    private final String description;
}
