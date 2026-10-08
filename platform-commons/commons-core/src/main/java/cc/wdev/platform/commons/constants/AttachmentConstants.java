package cc.wdev.platform.commons.constants;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author elvea
 */
public abstract class AttachmentConstants {
    /**
     * 默认的图片附件扩展名
     */
    public static List<String> DEFAULT_IMG_EXT = Lists.newArrayList("jpg", "jpeg", "gif", "png");
    /**
     * 默认的视频附件扩展名
     */
    public static List<String> DEFAULT_VIDEO_EXT = Lists.newArrayList("mp4");
    /**
     * 默认的音频附件扩展名
     */
    public static List<String> DEFAULT_AUDIO_EXT = Lists.newArrayList("mp3");
    /**
     * 默认的文档附件扩展名
     */
    public static List<String> DEFAULT_OFFICE_EXT = Lists.newArrayList("doc", "docx", "xls", "xlsx", "ppt", "pptx");
    /**
     * 默认的压缩文档附件扩展名
     */
    public static List<String> DEFAULT_ZIP_EXT = Lists.newArrayList("zip");
    /**
     * 默认的PDF文档附件扩展名
     */
    public static List<String> DEFAULT_PDF_EXT = Lists.newArrayList("pdf");
    /**
     * 默认编辑器附件扩展名
     */
    public static List<String> DEFAULT_EDITOR_EXT = Lists.newArrayList("java", "class", "jar", "war", "xml", "properties", "gradle", "mvn", "iml");
    /**
     * 默认的附件扩展名
     */
    public static List<String> DEFAULT_EXT = Lists.newArrayList();

    static {
        DEFAULT_EXT.addAll(DEFAULT_IMG_EXT);
        DEFAULT_EXT.addAll(DEFAULT_AUDIO_EXT);
        DEFAULT_EXT.addAll(DEFAULT_OFFICE_EXT);
        DEFAULT_EXT.addAll(DEFAULT_ZIP_EXT);
        DEFAULT_EXT.addAll(DEFAULT_PDF_EXT);

        DEFAULT_EDITOR_EXT.addAll(DEFAULT_IMG_EXT);
        DEFAULT_EDITOR_EXT.addAll(DEFAULT_VIDEO_EXT);
        DEFAULT_EDITOR_EXT.addAll(DEFAULT_AUDIO_EXT);
    }

}
