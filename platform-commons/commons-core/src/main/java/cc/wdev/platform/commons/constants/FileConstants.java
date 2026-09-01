package cc.wdev.platform.commons.constants;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author elvea
 */
public interface FileConstants {

    Set<String> DOCUMENT_EXTENSIONS = Set.of(
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
        "odt", "ods", "odp", "rtf", "html", "htm", "xml", "epub",
        "txt", "md", "markdown", "csv", "json", "yaml", "yml"
    );

    Set<String> VIDEO_EXTENSIONS = Set.of(
        "mp4", "m4v", "avi", "mov", "mkv", "flv", "wmv", "webm",
        "mpg", "mpeg", "ts", "mts", "m2ts", "3gp", "rmvb", "rm", "f4v", "vob", "ogv"
    );

    Set<String> AUDIO_EXTENSIONS = Set.of(
        "mp3", "wav", "m4a", "aac", "flac", "ogg", "opus", "wma",
        "amr", "aiff", "aif", "ape", "ac3", "mid", "midi"
    );

    Set<String> MEDIA_EXTENSIONS = Stream.concat(VIDEO_EXTENSIONS.stream(), AUDIO_EXTENSIONS.stream()).collect(Collectors.toSet());

    Set<String> IMAGE_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "tif", "ico", "svg", "heic", "heif"
    );

}
