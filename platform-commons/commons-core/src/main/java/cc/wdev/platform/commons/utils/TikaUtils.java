package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * @author elvea
 */
@Slf4j
public abstract class TikaUtils {

    private static final boolean TIKA_PRESENT = ClassUtils.isPresent("org.apache.tika.Tika", TikaUtils.class.getClassLoader());

    public static boolean isTikaPresent() {
        return TIKA_PRESENT;
    }

    /**
     * 交给Tika检测文件类型
     */
    public static MediaTypeCategoryEnum detect(Resource resource) {
        if (isTikaPresent()) {
            try (InputStream in = resource.getInputStream()) {
                String mime = new Tika().detect(in);
                if (!StringUtils.hasText(mime)) {
                    return null;
                }
                return FileUtils.detectByContentType(mime);
            } catch (Exception e) {
                log.warn("cannot sniff resource content, fallback to unsupported: {}", e.getMessage());
            }
        }
        return null;
    }

}
