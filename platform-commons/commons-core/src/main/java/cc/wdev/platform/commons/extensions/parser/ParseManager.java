package cc.wdev.platform.commons.extensions.parser;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.extensions.parser.domain.ParseOptions;
import cc.wdev.platform.commons.extensions.parser.domain.ParseRequest;
import cc.wdev.platform.commons.extensions.parser.domain.ParseResult;
import cc.wdev.platform.commons.extensions.parser.exception.ParseException;
import cc.wdev.platform.commons.extensions.parser.exception.UnsupportedFileTypeException;
import cc.wdev.platform.commons.extensions.parser.helpers.FfmpegHelper;
import cc.wdev.platform.commons.extensions.parser.helpers.TesseractHelper;
import cc.wdev.platform.commons.extensions.parser.helpers.TikaHelper;
import cc.wdev.platform.commons.extensions.parser.impl.FfmpegMediaParser;
import cc.wdev.platform.commons.extensions.parser.impl.TesseractImageParser;
import cc.wdev.platform.commons.extensions.parser.impl.TikaDocumentParser;
import cc.wdev.platform.commons.extensions.parser.utils.ParseUtils;
import cc.wdev.platform.commons.utils.TikaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class ParseManager implements InitializingBean {

    private final ParseConfig config;

    private final List<Parser> parsers = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        if (ParseUtils.isJavaCvPresent() && ParseUtils.isFfmpegPresent()) {
            this.parsers.add(new FfmpegMediaParser(this));
        }
        if (ParseUtils.isJavaCvPresent() && ParseUtils.isTesseractPresent() && TikaUtils.isTikaPresent()) {
            this.parsers.add(new TikaDocumentParser(this));
        }
        if (ParseUtils.isJavaCvPresent() && ParseUtils.isTesseractPresent()) {
            this.parsers.add(new TesseractImageParser(this));
        }
        // 同一分类存在多个解析器时，按 Parser#getOrder 排序，越小越优先
        this.parsers.sort(Comparator.comparingInt(Parser::getOrder));
    }

    public FfmpegHelper getFfmpegHelper() {
        return new FfmpegHelper(this.config);
    }

    public TesseractHelper getTesseractHelper() {
        return new TesseractHelper(this.config);
    }

    public TikaHelper getTikaHelper() {
        return new TikaHelper(this.config, this.getTesseractHelper());
    }

    public boolean supports(ParseRequest request) {
        return this.parsers.stream().anyMatch(parser -> parser.supports(request));
    }

    public ParseResult parse(ParseRequest request) throws ParseException {
        if (request == null || request.getResource() == null) {
            throw new ParseException(ResponseCodeEnum.PARAM_ERROR, "parse request or resource is null");
        }
        ParseRequest normalized = this.normalizeOptions(request);
        this.checkSizeLimit(normalized);
        for (Parser parser : this.parsers) {
            if (parser.supports(normalized)) {
                ParseResult result = parser.parse(normalized);
                if (result == null) {
                    throw new ParseException(
                        ResponseCodeEnum.PARSE_FAILED,
                        "parser returned null result: " + parser.getClass().getSimpleName()
                    );
                }
                if (result.getCategory() == null && parser.category() != null) {
                    result.setCategory(parser.category());
                }
                return result;
            }
        }
        throw new UnsupportedFileTypeException("unsupported file type: " + request.getOriginalFilename());
    }

    /**
     * 用全局配置补齐请求中为null的解析选项
     */
    private ParseRequest normalizeOptions(ParseRequest request) {
        MediaTypeCategoryEnum category = ParseUtils.detect(request);
        ParseConfig.Document document = this.config.getDocument();
        ParseConfig.Media media = this.config.getMedia();
        long defaultMaxSize = getDefaultMaxSize(category);

        ParseOptions options = request.getOptions();
        if (options == null) {
            options = ParseOptions.builder().build();
        }
        request.setOptions(ParseOptions.builder()
            .maxFileSize(options.getMaxFileSize() != null ? options.getMaxFileSize() : defaultMaxSize)
            .maxTextLength(options.getMaxTextLength() != null ? options.getMaxTextLength() : document.getMaxTextLength())
            .ocrEnabled(options.getOcrEnabled() != null ? options.getOcrEnabled() : document.isOcrEnabled())
            .extractEmbedded(options.getExtractEmbedded() != null ? options.getExtractEmbedded() : document.isExtractEmbedded())
            .timeoutSeconds(options.getTimeoutSeconds() != null ? options.getTimeoutSeconds() : document.getTimeoutSeconds())
            .targetSampleRate(options.getTargetSampleRate() != null ? options.getTargetSampleRate() : media.getTargetSampleRate())
            .targetChannels(options.getTargetChannels() != null ? options.getTargetChannels() : media.getTargetChannels())
            .maxDurationSeconds(options.getMaxDurationSeconds() != null ? options.getMaxDurationSeconds() : media.getMaxDurationSeconds())
            .build());
        return request;
    }

    private void checkSizeLimit(ParseRequest request) {
        ParseOptions options = request.getOptions();
        long maxFileSize = options != null && options.getMaxFileSize() != null ? options.getMaxFileSize() : 0;
        if (maxFileSize <= 0) {
            return;
        }
        try {
            long size = request.getResource().contentLength();
            if (size > maxFileSize) {
                throw new ParseException(
                    ResponseCodeEnum.PARSE_TOO_LARGE,
                    "file size " + size + " exceeds limit " + maxFileSize + " bytes"
                );
            }
        } catch (IOException e) {
            log.debug("cannot determine resource size, skip size check: {}", e.getMessage());
        }
    }

    private long getDefaultMaxSize(MediaTypeCategoryEnum category) {
        ParseConfig.Document document = this.config.getDocument();
        ParseConfig.Media media = this.config.getMedia();
        return (category == MediaTypeCategoryEnum.MEDIA) ? media.getMaxFileSize() : document.getMaxFileSize();
    }

}
