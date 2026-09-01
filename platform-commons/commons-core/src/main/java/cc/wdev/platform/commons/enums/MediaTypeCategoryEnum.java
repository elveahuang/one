package cc.wdev.platform.commons.enums;

import cc.wdev.platform.commons.constants.FileConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum MediaTypeCategoryEnum implements BaseEnum<String> {
    IMAGE("IMAGE", FileConstants.IMAGE_EXTENSIONS, "图片"),
    DOCUMENT("DOCUMENT", FileConstants.DOCUMENT_EXTENSIONS, "文档"),
    MEDIA("MEDIA", FileConstants.MEDIA_EXTENSIONS, "多媒体文件（包含音频和视频）"),
    UNSUPPORTED("UNSUPPORTED", Collections.emptySet(), "未支持类型");

    private final String value;
    private final Set<String> extensions;
    private final String description;
}
