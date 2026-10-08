package cc.wdev.platform.commons.extensions.parser.domain;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cc.wdev.platform.commons.utils.FileUtils;
import lombok.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;

/**
 * 解析请求
 *
 * @author elvea
 */
@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ParseRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件来源，要求可重复 {@link Resource#getInputStream()}（File / classpath / URL 资源均可）
     */
    private Resource resource;

    /**
     * 原始文件名，用于类型探测
     */
    private String originalFilename;

    /**
     * 文件 Content-Type，类型探测的兜底依据
     */
    private String contentType;

    /**
     * 可选参数，null 时由 ParserManager 按全局配置补齐
     */
    private ParseOptions options;

    /**
     * 探测结果缓存
     */
    private MediaTypeCategoryEnum detectedCategory;

    public static ParseRequest of(File file) {
        return ParseRequest.of(file, null);
    }

    public static ParseRequest of(File file, ParseOptions options) {
        return ParseRequest.builder()
            .resource(new FileSystemResource(file))
            .originalFilename(file.getName())
            .contentType(FileUtils.getContentType(file))
            .options(options)
            .build();
    }

}
