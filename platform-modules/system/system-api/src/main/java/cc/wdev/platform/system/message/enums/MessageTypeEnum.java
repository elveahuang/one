package cc.wdev.platform.system.message.enums;

import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import cc.wdev.platform.system.message.base.BaseMessageTypeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消息业务类型
 *
 * @author elvea
 */
@Getter
@RequiredArgsConstructor
public enum MessageTypeEnum implements BaseMessageTypeEnum {
    TEST("TEST", "测试消息", BizScopeTypeEnum.PLATFORM.getCode(), "测试消息"),
    LOGIN("LOGIN", "登录提醒", BizScopeTypeEnum.SYSTEM.getCode(), "登录提醒消息"),
    CAPTCHA("CAPTCHA", "验证码消息", BizScopeTypeEnum.SYSTEM.getCode(), "验证码消息"),
    REGISTER_SUCCESS("REGISTER_SUCCESS", "注册成功消息", BizScopeTypeEnum.SYSTEM.getCode(), "注册成功消息"),
    ;

    private final String value;
    private final String title;
    private final String scope;
    private final String description;

    @Override
    public String getLabelKey() {
        return ("label__message_type__%s".formatted(getValue())).toLowerCase();
    }

    @Override
    public String getGroup() {
        return CoreBizGroupTypeEnum.MESSAGE_TYPE.getValue().toUpperCase();
    }

}
