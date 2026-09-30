package cc.wdev.platform.system.region.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 地址实体关联业务类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AddressRelationBizTypeEnum implements BaseEnum<String> {
    NONE("NONE", "未指定");

    private final String value;
    private final String description;
}
