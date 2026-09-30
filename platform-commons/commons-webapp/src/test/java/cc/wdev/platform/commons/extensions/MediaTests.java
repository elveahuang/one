package cc.wdev.platform.commons.extensions;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MP3;

/**
 * @author elvea
 */
@Slf4j
public class MediaTests {

    @Test
    public void loaderTest() {
        Properties properties = Loader.loadProperties();
        log.info(properties.getProperty("platform.extension"));
        log.info(properties.getProperty("platform"));
        log.info(System.getProperty("java.library.path"));
        Loader.load(org.bytedeco.ffmpeg.global.avutil.class);
    }

    @Test
    public void videoTest() throws Exception {
        File root = new File("temp" + File.separator);
        File video = new File(root.getAbsolutePath(), "video.mp4");
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video)) {
            grabber.start();
            Frame frame;
            while ((frame = grabber.grabImage()) != null) {
                log.info("frame: {}", frame);
            }
            grabber.stop();
        }
    }

    @Test
    public void ffmpegTest() throws Exception {
        File root = new File("temp" + File.separator);
        File video = new File(root.getAbsolutePath(), "video.mp4");
        File audio = new File(root.getAbsolutePath(), "audio.mp3");
        String inputFilePath = video.getAbsolutePath();
        String outputFilePath = audio.getAbsolutePath();

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath)) {
            grabber.start();

            log.info("Video getAudioChannels : {}", grabber.getAudioChannels());

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFilePath, grabber.getAudioChannels())) {
                recorder.setAudioCodec(AV_CODEC_ID_MP3);
                recorder.setAudioBitrate(grabber.getAudioBitrate());
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.start();

                Frame frame;
                while ((frame = grabber.grabSamples()) != null) {
                    recorder.record(frame);
                }
                recorder.stop();
            }
            grabber.stop();
        }
    }

}
