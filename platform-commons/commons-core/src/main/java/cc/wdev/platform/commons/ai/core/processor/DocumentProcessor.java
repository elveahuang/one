package cc.wdev.platform.commons.ai.core.processor;

import cc.wdev.platform.commons.ai.config.SplittingConfig;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.commons.utils.FileUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
public class DocumentProcessor {

    public static List<Document> extract(File file) {
        return getDocumentReader(file).read();
    }

    public static List<Document> split(String text, SplittingConfig config, Map<String, Object> metadata) {
        if (StringUtils.isEmpty(text)) {
            return List.of();
        }
        Document document = Document.builder()
            .text(text)
            .metadata(MapUtils.isEmpty(metadata) ? Maps.newHashMap() : Maps.newHashMap(metadata))
            .build();
        TextSplitter splitter = AiUtils.getDocumentTransformer(config);
        return splitter.split(document);
    }

    public static DocumentReader getDocumentReader(File file) {
        String contentType = nvl(FileUtils.getContentType(file)).toLowerCase(Locale.ROOT);

        Resource resource = new FileSystemResource(file);
        if (contentType.contains("pdf")) {
            return new TikaDocumentReader(new FileSystemResource(file));
        } else if (contentType.contains("word") || contentType.contains("docx")
            || contentType.contains("excel") || contentType.contains("xlsx")
            || contentType.contains("powerpoint") || contentType.contains("pptx")) {
            return new TikaDocumentReader(new FileSystemResource(file));
        } else if (contentType.contains("markdown")) {
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(false)
                .build();
            return new MarkdownDocumentReader(new FileSystemResource(file), config);
        } else if (contentType.contains("html")) {
            return new JsoupDocumentReader(new FileSystemResource(file));
        } else {
            return new TextReader(resource);
        }
    }

}
