package cc.wdev.platform.commons.extensions.parser.domain;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 解析结果。
 * <p>
 * 文档类：{@link #text} + {@link #documentInfo}；音视频类：{@link #mediaFile} + {@link #mediaInfo}。
 * 抽取出的音频为临时文件，调用方负责转存（如存储服务）并在使用后清理。
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParseResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private MediaTypeCategoryEnum category;

    /**
     * 解析出的文本（文档）
     */
    private String text;

    /**
     * 抽取出的音频临时文件（音视频，WAV），可能为 null（如视频无音轨）
     */
    private File mediaFile;

    /**
     * 媒体探测信息
     */
    private MediaInfo mediaInfo;

    /**
     * 文档元信息
     */
    private DocumentInfo documentInfo;

    /**
     * 通用元数据（title / author / language / contentType 等）
     */
    private Map<String, String> metadata;

    /**
     * 解析过程中的非致命告警（如"无音轨""文本被截断"）
     */
    private List<String> warnings;

    /**
     * 文本是否因长度上限被截断
     */
    private boolean truncated;

}
