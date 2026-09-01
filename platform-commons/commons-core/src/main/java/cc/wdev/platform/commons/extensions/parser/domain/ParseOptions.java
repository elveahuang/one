package cc.wdev.platform.commons.extensions.parser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 单次解析的可选参数。
 * <p>
 * 字段为 null 时由 {@code ParserManager} 使用全局配置补齐，null 语义表示"跟随全局配置"。
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParseOptions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件大小上限（字节），null 时按分类使用全局配置（文档/媒体上限不同）
     */
    private Long maxFileSize;

    /**
     * 文档文本长度上限（字符数）
     */
    private Long maxTextLength;

    /**
     * 是否启用嵌入图片 OCR（仅文档解析）
     */
    private Boolean ocrEnabled;

    /**
     * 是否抽取嵌入文档/图片（仅文档解析）
     */
    private Boolean extractEmbedded;

    /**
     * 解析超时（秒）
     */
    private Integer timeoutSeconds;

    /**
     * 抽取音轨的目标采样率（Hz）
     */
    private Integer targetSampleRate;

    /**
     * 抽取音轨的目标声道数
     */
    private Integer targetChannels;

    /**
     * 媒体最大处理时长（秒），0 表示不限制
     */
    private Long maxDurationSeconds;

}
