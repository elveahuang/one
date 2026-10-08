package cc.wdev.platform.system.message.enums;

import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import cc.wdev.platform.system.message.base.BaseMessageChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息通道
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum MessageChannelEnum implements BaseMessageChannelEnum {
    NOTICE("NOTICE", "系统通知", "系统通知", MessageTemplateTypeEnum.HTML.getValue(), Boolean.FALSE),
    MAIL("MAIL", "邮件", "邮件", MessageTemplateTypeEnum.HTML.getValue(), Boolean.TRUE),
    SMS("SMS", "短信", "短信", MessageTemplateTypeEnum.TEMPLATE.getValue(), Boolean.TRUE),
    WX_MP("WX_MP", "微信公众号", "微信公众号", MessageTemplateTypeEnum.TEMPLATE.getValue(), Boolean.TRUE),
    WX_CP("WX_CP", "企业微信", "企业微信", MessageTemplateTypeEnum.HTML.getValue(), Boolean.FALSE),
    LARK("LARK", "飞书", "飞书", MessageTemplateTypeEnum.JSON.getValue(), Boolean.FALSE),
    DINGTALK("DINGTALK", "钉钉", "钉钉", MessageTemplateTypeEnum.JSON.getValue(), Boolean.FALSE);

    private final String value;
    private final String title;
    private final String description;
    private final String templateType;
    private final Boolean enabled;

    @Override
    public String getLabelKey() {
        return ("label__message_channel__%s".formatted(getValue())).toLowerCase();
    }

    @Override
    public String getGroup() {
        return CoreBizGroupTypeEnum.MESSAGE_CHANNEL_TYPE.getValue().toUpperCase();
    }

}
