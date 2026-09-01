package cc.wdev.platform.system.job.enums;

import cc.wdev.platform.commons.enums.BaseEnum;

/**
 * @author elvea
 */
public interface BaseJobItemTypeEnum extends BaseEnum<String> {

    @Override
    default String getValue() {
        return this.getCode();
    }

    String getCode();

    String getClassName();

    String getType();

    String getUnit();

    Integer getPeriod();

    Integer getHour();

    Integer getMinute();

    String getDescription();

}
