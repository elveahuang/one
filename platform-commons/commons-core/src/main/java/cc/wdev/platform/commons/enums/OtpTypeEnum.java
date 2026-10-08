package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum OtpTypeEnum implements BaseEnum<String> {
    SMS("SMS", "手机短信验证码"),
    EMAIL("EMAIL", "电子邮件验证码");

    private final String value;
    private final String description;
}
