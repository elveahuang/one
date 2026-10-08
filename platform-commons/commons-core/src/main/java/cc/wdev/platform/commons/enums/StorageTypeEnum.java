package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum StorageTypeEnum implements BaseEnum<String> {
    LOCAL("LOCAL", "本地存储"),
    AWS("AWS", "亚马逊对象存储，支持S3协议存储方案"),
    OSS("OSS", "阿里云对象存储");

    private final String value;
    private final String description;
}
