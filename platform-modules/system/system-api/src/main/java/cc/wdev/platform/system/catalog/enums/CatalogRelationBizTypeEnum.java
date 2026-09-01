package cc.wdev.platform.system.catalog.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 目录关联业务类型
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum CatalogRelationBizTypeEnum implements BaseEnum<String> {
    CATALOG_PARENT_CATALOG("CATALOG_PARENT_CATALOG", "目录<->目录关联"),
    NONE("NONE", "NONE");

    private final String value;
    private final String description;
}
