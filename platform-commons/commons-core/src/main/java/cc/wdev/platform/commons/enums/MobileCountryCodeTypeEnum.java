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
public enum MobileCountryCodeTypeEnum implements BaseEnum<String> {
    ZH_CN("86", "中国大陆"),
    ZH_HK("852", "中国香港"),
    ZH_TW("886", "中国台湾"),
    ZH_MO("853", "中国澳门");

    private final String value;
    private final String description;
}
