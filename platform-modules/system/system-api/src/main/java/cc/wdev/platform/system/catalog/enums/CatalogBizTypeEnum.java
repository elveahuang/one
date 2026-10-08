package cc.wdev.platform.system.catalog.enums;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 目录类型
 * SYSTEM - 系统目录
 * CUSTOM - 自定义目录
 * TEMPLATE - 模板目录
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum CatalogBizTypeEnum implements BaseBizTypeEnum {
    NONE("NONE", BizScopeTypeEnum.SYSTEM.getCode(), "NONE");

    private final String value;
    private final String scope;
    private final String description;

    @Override
    public String getGroup() {
        return CoreBizGroupTypeEnum.CATALOG_TYPE.getValue().toUpperCase();
    }

}
