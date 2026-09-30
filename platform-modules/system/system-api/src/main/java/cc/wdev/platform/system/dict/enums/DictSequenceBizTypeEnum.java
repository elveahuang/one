package cc.wdev.platform.system.dict.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典排序业务类型
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum DictSequenceBizTypeEnum implements BaseEnum<String> {
    NONE("NONE", "NONE");

    private final String value;
    private final String description;
}
