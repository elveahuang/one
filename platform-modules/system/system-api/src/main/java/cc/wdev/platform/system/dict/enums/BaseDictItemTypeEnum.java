package cc.wdev.platform.system.dict.enums;

import cc.wdev.platform.commons.enums.BaseEnum;

/**
 * @author elvea
 */
public interface BaseDictItemTypeEnum extends BaseEnum<String> {

    String getCode();

    String getTitle();

    String getType();

    Integer getIdx();

    @Override
    default String getValue() {
        return this.getCode();
    }

}
