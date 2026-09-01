package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiSplittingStrategy implements BaseEnum<String> {
    TOKEN("TOKEN", "TokenTextSplitter");

    private final String value;

    private final String description;

}
