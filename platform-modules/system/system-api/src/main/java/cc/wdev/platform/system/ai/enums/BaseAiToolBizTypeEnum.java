package cc.wdev.platform.system.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;

/**
 * @author elvea
 */
public interface BaseAiToolBizTypeEnum extends BaseEnum<String> {

    String getName();

    String getToolName();

    String getClassName();

    String getMethodName();

    @Override
    default String getValue() {
        return getClassName() + "#" + getMethodName();
    }

}
