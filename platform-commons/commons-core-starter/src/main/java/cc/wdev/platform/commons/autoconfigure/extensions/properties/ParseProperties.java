package cc.wdev.platform.commons.autoconfigure.extensions.properties;

import cc.wdev.platform.commons.extensions.parser.ParseConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(ParseProperties.PREFIX)
public class ParseProperties {

    public static final String PREFIX = "platform.parser";

    private boolean enabled = true;

    /**
     * 调试模式
     */
    @NestedConfigurationProperty
    private ParseConfig.Debug debug = ParseConfig.Debug.builder().build();

    /**
     * Tesseract OCR 配置
     */
    @NestedConfigurationProperty
    private ParseConfig.Tesseract tesseract = ParseConfig.Tesseract.builder().build();

    /**
     * 文档解析配置
     */
    @NestedConfigurationProperty
    private ParseConfig.Document document = ParseConfig.Document.builder().build();

    /**
     * 媒体解析配置
     */
    @NestedConfigurationProperty
    private ParseConfig.Media media = ParseConfig.Media.builder().build();

}
