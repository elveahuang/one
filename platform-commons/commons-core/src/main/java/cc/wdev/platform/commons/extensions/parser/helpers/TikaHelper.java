package cc.wdev.platform.commons.extensions.parser.helpers;

import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.extensions.parser.ParseConfig;
import cc.wdev.platform.commons.extensions.parser.domain.DocumentParseResult;
import cc.wdev.platform.commons.extensions.parser.domain.ParseOptions;
import cc.wdev.platform.commons.extensions.parser.exception.ParseException;
import cn.hutool.core.io.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.metadata.PagedText;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

/**
 * Apache Tika 文档解析实现
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class TikaHelper {

    private static final int DEFAULT_MAX_TEXT_LENGTH = 2_000_000;

    private static final int DEFAULT_TIMEOUT_SECONDS = 60;

    /**
     * 解析线程池
     * Tika对损坏文件可能长时间不返回，用超时兜底；线程池有界，任务排队满后由调用线程
     * 直接执行（CallerRunsPolicy），避免损坏文件无限堆积线程。
     */
    private static final int PARSE_CORE_POOL_SIZE = 2;

    private static final int PARSE_MAX_POOL_SIZE = 4;

    private static final int PARSE_QUEUE_CAPACITY = 100;

    private static final ExecutorService PARSE_EXECUTOR = new ThreadPoolExecutor(
        PARSE_CORE_POOL_SIZE,
        PARSE_MAX_POOL_SIZE,
        60L,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(PARSE_QUEUE_CAPACITY),
        runnable -> {
            Thread thread = new Thread(runnable, "tika-parse");
            thread.setDaemon(true);
            return thread;
        },
        new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private final ParseConfig config;

    private final TesseractHelper tesseractHelper;

    public String parse(File file) throws Exception {
        return this.parse(new FileSystemResource(file), ParseOptions.builder().build()).getText();
    }

    public DocumentParseResult parse(Resource resource, ParseOptions options) throws Exception {
        int timeout = options != null && options.getTimeoutSeconds() != null
            ? options.getTimeoutSeconds()
            : DEFAULT_TIMEOUT_SECONDS;
        try {
            return PARSE_EXECUTOR
                .submit(() -> this.doParse(resource, options))
                .get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new ParseException(ResponseCodeEnum.PARSE_TIMEOUT, "document parse timeout", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ParseException parseException) {
                throw parseException;
            }
            throw new ParseException(ResponseCodeEnum.PARSE_FAILED, "document parse failed", cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ParseException(ResponseCodeEnum.PARSE_FAILED, "document parse interrupted", e);
        }
    }

    private DocumentParseResult doParse(Resource resource, ParseOptions options) throws Exception {
        boolean ocrEnabled = options == null || options.getOcrEnabled() == null || options.getOcrEnabled();
        boolean extractEmbedded = options == null || options.getExtractEmbedded() == null || options.getExtractEmbedded();
        long maxTextLength = options != null && options.getMaxTextLength() != null
            ? options.getMaxTextLength()
            : DEFAULT_MAX_TEXT_LENGTH;

        String detectedType;
        try (InputStream in = resource.getInputStream()) {
            detectedType = new Tika().detect(in, resource.getFilename());
        }
        log.debug("detected document type: {}, resource: {}", detectedType, resource.getFilename());

        Map<String, File> embeddedImages = new HashMap<>();
        List<String> pendingImages = new ArrayList<>();
        OcrContentHandler handler = new OcrContentHandler(pendingImages);
        Metadata metadata = new Metadata();

        try (InputStream in = resource.getInputStream()) {
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();
            context.set(EmbeddedDocumentExtractor.class, new EmbeddedDocumentExtractor() {

                @Override
                public boolean shouldParseEmbedded(Metadata embeddedMetadata) {
                    return extractEmbedded;
                }

                @Override
                public void parseEmbedded(InputStream stream,
                                          ContentHandler contentHandler,
                                          Metadata embeddedMetadata,
                                          boolean outputHtml) throws IOException {
                    String name = embeddedMetadata.get(TikaCoreProperties.RESOURCE_NAME_KEY);
                    String contentType = embeddedMetadata.get(Metadata.CONTENT_TYPE);
                    if (name == null || contentType == null || !contentType.startsWith("image/")) {
                        return;
                    }
                    File image = File.createTempFile("tika_embed_", "." + extensionOf(name));
                    FileUtil.writeFromStream(stream, image);
                    embeddedImages.put("embedded:" + name, image);
                }
            });
            parser.parse(in, handler, metadata, context);
        }

        String text = handler.getText();
        boolean truncated = false;
        if (text.length() > maxTextLength) {
            text = text.substring(0, (int) maxTextLength);
            truncated = true;
        }

        // 嵌入图片 OCR：替换占位符，只清理本次解析产生的临时图片
        for (String src : pendingImages) {
            File image = embeddedImages.get(src);
            if (image == null) {
                continue;
            }
            String ocrText = "";
            if (ocrEnabled && this.tesseractHelper != null) {
                try {
                    ocrText = this.tesseractHelper.parse(image);
                } catch (Exception e) {
                    log.warn("embedded image OCR failed: {}", e.getMessage());
                }
            }
            text = text.replace(src, ocrText);
            FileUtil.del(image);
        }

        Map<String, String> meta = new HashMap<>();
        putIfPresent(meta, "title", metadata.get(TikaCoreProperties.TITLE));
        putIfPresent(meta, "author", metadata.get(TikaCoreProperties.CREATOR));
        putIfPresent(meta, "language", metadata.get(TikaCoreProperties.LANGUAGE));
        putIfPresent(meta, "contentType", metadata.get(Metadata.CONTENT_TYPE));
        Integer pageCount = parsePageCount(
            metadata.get(PagedText.N_PAGES),
            metadata.get(Office.PAGE_COUNT)
        );

        return DocumentParseResult.builder()
            .text(text)
            .metadata(meta)
            .pageCount(pageCount)
            .truncated(truncated)
            .build();
    }

    private static void putIfPresent(Map<String, String> target, String key, String value) {
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
    }

    private static Integer parsePageCount(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException ignored) {
                // 继续尝试下一个来源
            }
        }
        return null;
    }

    private static String extensionOf(String name) {
        int idx = name.lastIndexOf('.');
        if (idx < 0 || idx == name.length() - 1) {
            return "img";
        }
        String ext = name.substring(idx + 1).toLowerCase(Locale.ROOT);
        return ext.matches("[a-z0-9]{1,8}") ? ext : "img";
    }

}
