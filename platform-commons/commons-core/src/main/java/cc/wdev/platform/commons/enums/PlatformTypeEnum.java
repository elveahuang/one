package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 国家地区区位码枚举
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum PlatformTypeEnum implements BaseEnum<String> {
    WECHAT("WECHAT", "微信"),
    WECOM("WECOM", "企微"),
    LARK("LARK", "飞书"),
    DINGTALK("DINGTALK", "飞书"),
    DESKTOP("WAP", "桌面浏览器"),
    WAP("WAP", "手机浏览器"),
    OTHERS("OTHERS", "其他");

    private final String value;
    private final String description;
}
