package cc.wdev.platform.system.dict.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典关联业务类型
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum DictRelationBizTypeEnum implements BaseEnum<String> {
    BANNER("BANNER", "BANNER"),
    LINK("LINK", "LINK"),
    LINK_CATALOG("LINK_CATALOG", "友情链接分类"),
    NONE("NONE", "NONE");

    private final String value;
    private final String description;
}
