package cc.wdev.platform.commons.extensions.parser.impl;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.extensions.parser.ParseManager;
import cc.wdev.platform.commons.extensions.parser.Parser;
import cc.wdev.platform.commons.extensions.parser.domain.DocumentInfo;
import cc.wdev.platform.commons.extensions.parser.domain.ParseOptions;
import cc.wdev.platform.commons.extensions.parser.domain.ParseRequest;
import cc.wdev.platform.commons.extensions.parser.domain.ParseResult;
import cc.wdev.platform.commons.extensions.parser.exception.ParseException;
import cc.wdev.platform.commons.extensions.parser.utils.ParseUtils;
import cc.wdev.platform.commons.utils.FileUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cn.hutool.core.io.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片解析器：基于 Tesseract OCR 识别图片中的文本（支持 chi_sim + eng）。
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class TesseractImageParser implements Parser {

    private static final long DEFAULT_MAX_TEXT_LENGTH = 2_000_000L;

    private final ParseManager parseManager;

    @Override
    public MediaTypeCategoryEnum category() {
        return MediaTypeCategoryEnum.IMAGE;
    }

    @Override
    public boolean supports(ParseRequest request) {
        return ParseUtils.detect(request) == MediaTypeCategoryEnum.IMAGE;
    }

    @Override
    public ParseResult parse(ParseRequest request) throws ParseException {
        File source = null;
        boolean tempSource = false;
        try {
            long startTime = System.currentTimeMillis();
            source = FileUtils.materialize(request.getResource());
            tempSource = !FileUtils.isFileResource(request.getResource());

            ParseOptions options = request.getOptions();
            boolean ocrEnabled = options == null || options.getOcrEnabled() == null || options.getOcrEnabled();
            long maxTextLength = options != null && options.getMaxTextLength() != null
                ? options.getMaxTextLength()
                : DEFAULT_MAX_TEXT_LENGTH;

            String text = "";
            List<String> warnings = null;
            if (ocrEnabled) {
                text = parseManager.getTesseractHelper().parse(source);
                if (StringUtils.isEmpty(text)) {
                    warnings = List.of("no text recognized");
                }
            } else {
                warnings = List.of("ocr disabled");
            }

            boolean truncated = false;
            if (text.length() > maxTextLength) {
                text = text.substring(0, (int) maxTextLength);
                truncated = true;
            }

            String contentType = request.getContentType() != null
                ? request.getContentType()
                : FileUtils.getContentType(source);
            Map<String, String> metadata = new HashMap<>();
            if (StringUtils.isNotEmpty(contentType)) {
                metadata.put("contentType", contentType);
            }

            ParseResult result = ParseResult.builder()
                .category(MediaTypeCategoryEnum.IMAGE)
                .text(text)
                .documentInfo(DocumentInfo.builder().contentType(contentType).build())
                .metadata(metadata)
                .warnings(warnings)
                .truncated(truncated)
                .build();
            log.debug("image parse done, file: {}, duration: {}ms, textLength: {}, truncated: {}",
                request.getOriginalFilename(),
                System.currentTimeMillis() - startTime,
                text.length(),
                truncated);
            return result;
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("image parse failed, file: {}", request.getOriginalFilename(), e);
            throw new ParseException(ResponseCodeEnum.PARSE_FAILED,
                "image parse failed: " + request.getOriginalFilename(), e);
        } finally {
            if (tempSource) {
                FileUtil.del(source);
            }
        }
    }

}
