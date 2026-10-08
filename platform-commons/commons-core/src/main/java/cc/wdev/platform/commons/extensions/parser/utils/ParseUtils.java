package cc.wdev.platform.commons.extensions.parser.utils;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cc.wdev.platform.commons.extensions.parser.domain.ParseRequest;
import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.FileUtils;
import cc.wdev.platform.commons.utils.TikaUtils;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;

import java.util.Properties;

/**
 * @author elvea
 */
@Slf4j
public abstract class ParseUtils {

    private static final boolean JAVACV_PRESENT = ClassUtils.isPresent("org.bytedeco.javacv.FFmpegFrameGrabber", ParseUtils.class.getClassLoader());

    private static final boolean FFMPEG_PRESENT = ClassUtils.isPresent("org.bytedeco.ffmpeg.global.avutil", ParseUtils.class.getClassLoader());

    private static final boolean TESSERACT_PRESENT = ClassUtils.isPresent("org.bytedeco.tesseract.TessBaseAPI", ParseUtils.class.getClassLoader());

    public static boolean isJavaCvPresent() {
        return JAVACV_PRESENT;
    }

    public static boolean isFfmpegPresent() {
        return FFMPEG_PRESENT;
    }

    public static boolean isTesseractPresent() {
        return TESSERACT_PRESENT;
    }

    public static void check() {
        System.setProperty("org.bytedeco.javacpp.logger.debug", "true");

        Properties properties = Loader.loadProperties();
        log.info(properties.getProperty("platform.extension"));
        log.info(properties.getProperty("platform"));
        log.info(System.getProperty("java.library.path"));

        Loader.load(org.bytedeco.ffmpeg.global.avutil.class);
        Loader.load(org.bytedeco.tesseract.global.tesseract.class);
        Loader.load(org.bytedeco.leptonica.global.leptonica.class);
    }

    /**
     * 探测文件分类
     * 1. 根据文件扩展名
     * 2. 根据文件Content-Type
     * 3. 根据Tika探测
     */
    public static MediaTypeCategoryEnum detect(ParseRequest request) {
        if (request == null) {
            return MediaTypeCategoryEnum.UNSUPPORTED;
        }
        if (request.getDetectedCategory() != null) {
            return request.getDetectedCategory();
        }
        MediaTypeCategoryEnum category = FileUtils.detectByExtension(request.getOriginalFilename());
        if (category == null) {
            category = FileUtils.detectByContentType(request.getContentType());
        }
        if (category == null) {
            category = TikaUtils.detect(request.getResource());
        }
        MediaTypeCategoryEnum detectedCategory = category != null ? category : MediaTypeCategoryEnum.UNSUPPORTED;
        request.setDetectedCategory(detectedCategory);
        return detectedCategory;
    }

}
