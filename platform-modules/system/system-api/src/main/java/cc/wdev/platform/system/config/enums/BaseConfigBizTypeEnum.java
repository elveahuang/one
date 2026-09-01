package cc.wdev.platform.system.config.enums;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;

/**
 * @author elvea
 */
public interface BaseConfigBizTypeEnum extends BaseBizTypeEnum {

    String getGroupType();

    String getContentType();

    @Override
    default String getGroup() {
        return CoreBizGroupTypeEnum.CONFIG_TYPE.getValue().toUpperCase();
    }

}
