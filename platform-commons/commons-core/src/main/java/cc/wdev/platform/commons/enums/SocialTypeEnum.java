package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum SocialTypeEnum implements BaseEnum<String> {
    LARK("LARK", "飞书"),
    WECHAT_MP("WECHATMP", "微信服务号"),
    WECHAT_MA("WECHATMA", "微信小程序"),
    WEWORK("WEWORK", "企微"),
    DINGTALK("DINGTALK", "钉钉");

    private final String value;
    private final String description;
}
