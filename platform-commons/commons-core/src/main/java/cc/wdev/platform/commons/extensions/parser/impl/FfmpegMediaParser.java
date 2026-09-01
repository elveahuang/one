package cc.wdev.platform.commons.extensions.parser.impl;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.extensions.parser.ParseManager;
import cc.wdev.platform.commons.extensions.parser.Parser;
import cc.wdev.platform.commons.extensions.parser.domain.*;
import cc.wdev.platform.commons.extensions.parser.exception.ParseException;
import cc.wdev.platform.commons.extensions.parser.utils.ParseUtils;
import cc.wdev.platform.commons.utils.FileUtils;
import cn.hutool.core.io.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * 音视频解析器：同时支持 VIDEO 与 AUDIO，探测媒体信息并抽取音轨（WAV）。
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class FfmpegMediaParser implements Parser {

    private final ParseManager parseManager;

    @Override
    public MediaTypeCategoryEnum category() {
        return MediaTypeCategoryEnum.MEDIA;
    }

    @Override
    public boolean supports(ParseRequest request) {
        return ParseUtils.detect(request) == MediaTypeCategoryEnum.MEDIA;
    }

    @Override
    public ParseResult parse(ParseRequest request) throws ParseException {
        File source = null;
        boolean tempSource = false;
        try {
            long startTime = System.currentTimeMillis();
            source = FileUtils.materialize(request.getResource());
            tempSource = !FileUtils.isFileResource(request.getResource());

            MediaInfo mediaInfo = parseManager.getFfmpegHelper().probe(source);
            File audio = parseManager.getFfmpegHelper().extractAudio(source, resolveExtractOptions(request));

            ParseResult.ParseResultBuilder builder = ParseResult.builder()
                .category(MediaTypeCategoryEnum.MEDIA)
                .mediaInfo(mediaInfo)
                .mediaFile(audio);
            if (audio == null) {
                builder.warnings(List.of("no audio stream"));
            }
            ParseResult result = builder.build();
            log.debug("media parse done, file: {}, duration: {}ms",
                request.getOriginalFilename(),
                System.currentTimeMillis() - startTime);
            return result;
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("media parse failed, file: {}", request.getOriginalFilename(), e);
            throw new ParseException(ResponseCodeEnum.PARSE_FAILED,
                "media parse failed: " + request.getOriginalFilename(), e);
        } finally {
            if (tempSource) {
                FileUtil.del(source);
            }
        }
    }

    private MediaExtractOptions resolveExtractOptions(ParseRequest request) {
        ParseOptions options = request.getOptions();
        return MediaExtractOptions.builder()
            .sampleRate(options != null && options.getTargetSampleRate() != null
                ? options.getTargetSampleRate()
                : 0)
            .channels(options != null && options.getTargetChannels() != null
                ? options.getTargetChannels()
                : 0)
            .maxDurationSeconds(options != null && options.getMaxDurationSeconds() != null
                ? options.getMaxDurationSeconds()
                : 0)
            .build();
    }

}
