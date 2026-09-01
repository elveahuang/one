package cc.wdev.platform.system.commons.constants;

import cc.wdev.platform.commons.enums.StorageAccessTypeEnum;
import cc.wdev.platform.system.storage.domain.biz.Config;
import com.google.common.collect.Lists;

import static java.util.Collections.emptyList;

/**
 * @author elvea
 */
public interface SystemAttachmentConstants {

    Config DEFAULT_CONFIG = Config.builder()
        .extensions(emptyList())
        .fileTypes(emptyList())
        .multiple(false)
        .build();

    Config DEFAULT_PRIVATE_IMAGE_CONFIG = Config.builder()
        .extensions(Lists.newArrayList("jpg", "jpeg", "png", "gif"))
        .fileTypes(Lists.newArrayList("image/jpeg", "image/png", "image/gif"))
        .multiple(false)
        .accessType(StorageAccessTypeEnum.PRIVATE.getValue())
        .build();

    Config DEFAULT_PUBLIC_IMAGE_CONFIG = Config.builder()
        .extensions(Lists.newArrayList("jpg", "jpeg", "png", "gif"))
        .fileTypes(Lists.newArrayList("image/jpeg", "image/png", "image/gif"))
        .multiple(false)
        .accessType(StorageAccessTypeEnum.PUBLIC.getValue())
        .build();

    Config DEFAULT_VIDEO_CONFIG = Config.builder()
        .extensions(Lists.newArrayList("mp4"))
        .fileTypes(Lists.newArrayList("video/mp4"))
        .multiple(false)
        .build();

    Config DEFAULT_AUDIO_CONFIG = Config.builder()
        .extensions(Lists.newArrayList("mp3"))
        .fileTypes(Lists.newArrayList("audio/mpeg"))
        .multiple(false)
        .build();

    Config DEFAULT_OFFICE_CONFIG = Config.builder()
        .extensions(Lists.newArrayList("docx", "xlsx", "pptx"))
        .fileTypes(Lists.newArrayList())
        .multiple(false)
        .build();

    Config DEFAULT_PDF_CONFIG = Config.builder()
        .extensions(Lists.newArrayList("pdf"))
        .fileTypes(Lists.newArrayList("application/pdf"))
        .multiple(false)
        .build();

    Config DEFAULT_DOC_CONFIG = Config.builder()
        .extensions(Lists.newArrayList("pdf"))
        .fileTypes(Lists.newArrayList("application/pdf"))
        .multiple(false)
        .build();

}
