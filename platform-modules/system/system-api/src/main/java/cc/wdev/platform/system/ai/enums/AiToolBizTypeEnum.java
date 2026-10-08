package cc.wdev.platform.system.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiToolBizTypeEnum implements BaseAiToolBizTypeEnum {
    GET_APPLICATION_VERSION("getVersion", "CommonTools", "getVersion", "获取应用版本号", "获取应用版本号"),
    GET_CURRENT_DATETIME("getCurrentDateTime", "CommonTools", "getCurrentDateTime", "获取系统当前时间", "获取系统当前时间");

    private final String toolName;
    private final String className;
    private final String methodName;
    private final String name;
    private final String description;
}
