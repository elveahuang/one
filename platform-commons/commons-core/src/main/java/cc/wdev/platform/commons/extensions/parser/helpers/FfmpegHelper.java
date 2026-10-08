package cc.wdev.platform.commons.extensions.parser.helpers;

import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.extensions.parser.ParseConfig;
import cc.wdev.platform.commons.extensions.parser.domain.MediaExtractOptions;
import cc.wdev.platform.commons.extensions.parser.domain.MediaInfo;
import cc.wdev.platform.commons.extensions.parser.exception.ParseException;
import cn.hutool.core.io.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class FfmpegHelper {

    private static final int DEFAULT_SAMPLE_RATE = 16000;

    private static final int DEFAULT_CHANNELS = 1;

    private static final int DEFAULT_SAMPLE_FORMAT = avutil.AV_SAMPLE_FMT_S16;

    private static final long MICROS_PER_SECOND = 1_000_000L;

    private static final int LOG_INTERVAL_FRAMES = 500;

    private final ParseConfig config;

    public MediaInfo probe(File file) throws Exception {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            grabber.start();
            int sampleRate = grabber.getSampleRate();
            int channels = grabber.getAudioChannels();
            long durationMicros = grabber.getLengthInTime();
            return MediaInfo.builder()
                .format(grabber.getFormat())
                .durationSeconds(durationMicros > 0 ? durationMicros / (double) MICROS_PER_SECOND : null)
                .size(file.exists() ? file.length() : null)
                .hasVideo(grabber.getImageWidth() > 0 && grabber.getImageHeight() > 0)
                .hasAudio(grabber.getAudioStream() >= 0 && sampleRate > 0)
                .videoCodec(grabber.getVideoCodecName())
                .width(grabber.getImageWidth())
                .height(grabber.getImageHeight())
                .frameRate(grabber.getVideoFrameRate())
                .audioCodec(grabber.getAudioCodecName())
                .sampleRate(sampleRate)
                .channels(channels)
                .bitrate((long) grabber.getVideoBitrate() + grabber.getAudioBitrate())
                .build();
        }
    }

    public File extractAudio(File file, MediaExtractOptions options) throws Exception {
        int sampleRate = options != null && options.getSampleRate() > 0
            ? options.getSampleRate()
            : DEFAULT_SAMPLE_RATE;
        int channels = options != null && options.getChannels() > 0
            ? options.getChannels()
            : DEFAULT_CHANNELS;
        long maxDurationSeconds = options != null ? options.getMaxDurationSeconds() : 0;

        File temp;
        try {
            temp = Files.createTempFile("ffmpeg_audio_", ".wav").toFile();
        } catch (IOException e) {
            throw new ParseException(ResponseCodeEnum.PARSE_FAILED, "create temp audio file failed", e);
        }

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            grabber.setSampleRate(sampleRate);
            grabber.setAudioChannels(channels);
            grabber.setSampleFormat(DEFAULT_SAMPLE_FORMAT);
            grabber.start();

            if (grabber.getAudioStream() < 0) {
                log.warn("no audio stream in file: {}", file.getName());
                FileUtil.del(temp);
                return null;
            }

            try (WavPcmWriter writer = new WavPcmWriter(temp, sampleRate, channels)) {
                Frame frame;
                long frameCount = 0;
                boolean truncated = false;
                while ((frame = grabber.grabSamples()) != null) {
                    if (frame.samples == null || frame.samples.length == 0) {
                        continue;
                    }
                    ShortBuffer[] shortSamples = toShortSamples(frame.samples);
                    writer.write(shortSamples, shortSamples[0].remaining());
                    frameCount++;

                    long timestamp = grabber.getTimestamp();
                    if (maxDurationSeconds > 0
                        && timestamp > 0
                        && timestamp / (double) MICROS_PER_SECOND >= maxDurationSeconds) {
                        truncated = true;
                        log.info("audio extraction stopped at max duration {}s, file: {}",
                            maxDurationSeconds, file.getName());
                        break;
                    }
                    if (frameCount % LOG_INTERVAL_FRAMES == 0) {
                        log.debug("audio extraction progress: file={}, frames={}, timestamp={}ms",
                            file.getName(), frameCount, timestamp / 1000);
                    }
                }
                writer.finish();
                log.info("audio extracted from {}: frames={}, dataSize={} bytes, truncated={}",
                    file.getName(), frameCount, writer.getDataSize(), truncated);
            }
            return temp;
        } catch (ParseException e) {
            FileUtil.del(temp);
            throw e;
        } catch (Exception e) {
            FileUtil.del(temp);
            throw new ParseException(ResponseCodeEnum.PARSE_FAILED,
                "extract audio failed: " + file.getName(), e);
        }
    }

    private static ShortBuffer[] toShortSamples(Buffer[] samples) {
        ShortBuffer[] result = new ShortBuffer[samples.length];
        for (int i = 0; i < samples.length; i++) {
            if (!(samples[i] instanceof ShortBuffer shortBuffer)) {
                throw new IllegalStateException(
                    "unexpected sample format: " + samples[i].getClass().getName());
            }
            result[i] = shortBuffer;
        }
        return result;
    }

}
