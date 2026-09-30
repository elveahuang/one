package cc.wdev.platform.commons.extensions.parser.impl;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.extensions.parser.ParseManager;
import cc.wdev.platform.commons.extensions.parser.Parser;
import cc.wdev.platform.commons.extensions.parser.domain.DocumentInfo;
import cc.wdev.platform.commons.extensions.parser.domain.DocumentParseResult;
import cc.wdev.platform.commons.extensions.parser.domain.ParseRequest;
import cc.wdev.platform.commons.extensions.parser.domain.ParseResult;
import cc.wdev.platform.commons.extensions.parser.exception.ParseException;
import cc.wdev.platform.commons.extensions.parser.utils.ParseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class TikaDocumentParser implements Parser {

    private final ParseManager parseManager;

    @Override
    public MediaTypeCategoryEnum category() {
        return MediaTypeCategoryEnum.DOCUMENT;
    }

    @Override
    public boolean supports(ParseRequest request) {
        return ParseUtils.detect(request) == MediaTypeCategoryEnum.DOCUMENT;
    }

    @Override
    public ParseResult parse(ParseRequest request) throws ParseException {
        try {
            long startTime = System.currentTimeMillis();
            DocumentParseResult document = parseManager.getTikaHelper().parse(request.getResource(), request.getOptions());
            Map<String, String> metadata = document.getMetadata();
            DocumentInfo documentInfo = DocumentInfo.builder()
                .title(getMeta(metadata, "title"))
                .author(getMeta(metadata, "author"))
                .language(getMeta(metadata, "language"))
                .pageCount(document.getPageCount())
                .contentType(getMeta(metadata, "contentType"))
                .build();

            ParseResult result = ParseResult.builder()
                .category(MediaTypeCategoryEnum.DOCUMENT)
                .text(document.getText())
                .documentInfo(documentInfo)
                .metadata(metadata)
                .truncated(Boolean.TRUE.equals(document.getTruncated()))
                .build();
            log.debug("document parse done, file: {}, duration: {}ms, textLength: {}, truncated: {}",
                request.getOriginalFilename(),
                System.currentTimeMillis() - startTime,
                document.getText() != null ? document.getText().length() : 0,
                document.getTruncated());
            return result;
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("document parse failed, file: {}", request.getOriginalFilename(), e);
            throw new ParseException(ResponseCodeEnum.PARSE_FAILED,
                "document parse failed: " + request.getOriginalFilename(), e);
        }
    }

    private static String getMeta(Map<String, String> metadata, String key) {
        return metadata != null ? metadata.get(key) : null;
    }

}
