package cc.wdev.platform.commons.core.storage;

import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.core.storage.model.FileOptions;
import cc.wdev.platform.commons.utils.DateTimeUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Slf4j
public abstract class StorageUtils {

    public static String generateFilename(String filename) {
        return StringUtils.simpleUuid() + "." + FilenameUtils.getExtension(filename);
    }

    public static String generateFileKey(FileOptions options) {
        String suffix = FilenameUtils.getExtension(options.getOriginalFilename());
        String uuid = StringUtils.simpleUuid();
        String path = DateTimeUtils.format(LocalDateTime.now(), DateTimeConstants.Pattern.SIMPLE_DATE);
        if (StringUtils.isNotEmpty(options.getPrefix())) {
            path = options.getPrefix() + "/" + path;
        }
        return path + "/" + uuid + "." + suffix;
    }

}
