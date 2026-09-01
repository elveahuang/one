package cc.wdev.platform.system.tag.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 标签关联业务类型
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum TagRelationBizTypeEnum implements BaseEnum<String> {
    NONE("NONE", "NONE");

    private final String value;
    private final String description;
}
