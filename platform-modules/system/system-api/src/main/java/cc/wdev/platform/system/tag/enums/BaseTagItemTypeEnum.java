package cc.wdev.platform.system.tag.enums;

import cc.wdev.platform.commons.enums.BaseEnum;


/**
 * @author elvea
 */
public interface BaseTagItemTypeEnum extends BaseEnum<String> {

    String getTitle();

    String getType();

    Integer getIdx();

    @Override
    default String getValue() {
        return this.getTitle();
    }

}
