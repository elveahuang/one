package cc.wdev.platform.system.tag.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统默认初始的标签数据
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum TagItemTypeEnum implements BaseTagItemTypeEnum {
    ;
    private final String title;
    private final String type;
    private final Integer idx;
    private final String description;
}
