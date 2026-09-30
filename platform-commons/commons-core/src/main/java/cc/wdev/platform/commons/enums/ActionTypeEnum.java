package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum ActionTypeEnum implements BaseEnum<String> {
    SAVE("SAVE", "保存"),
    CREATE("CREATE", "创建"),
    UPDATE("UPDATE", "更新"),
    DELETE("DELETE", "删除");

    private final String value;
    private final String description;
}
