package cc.wdev.platform.system.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;

/**
 * @author elvea
 */
public interface BaseAiModelBizTypeEnum extends BaseEnum<String> {

    String getModelName();

    String getModelType();

    String getModelProvider();

    String getServiceProvider();

    @Override
    default String getValue() {
        return getModelProvider().toLowerCase() + "#" + getModelName().toLowerCase();
    }

}
