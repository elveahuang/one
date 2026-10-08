package cc.wdev.platform.commons.oapis.sms.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum SmsTypeEnum implements BaseEnum<String> {
    Tencent("Tencent", "腾讯短信服务"),
    Aliyun("Aliyun", "阿里短信服务"),
    None("None", "None");

    private final String value;
    private final String description;
}
