package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.util.Locale;

import static org.apache.commons.io.FileUtils.*;

/**
 * @author elvea
 */
@Slf4j
public abstract class FileUtils {

    public static final String SEPARATOR = FileSystems.getDefault().getSeparator();

    public static final FileNameMap FILE_NAME_MAP = URLConnection.getFileNameMap();

    /**
     * 删除文件
     */
    public static void delete(File file) {
        if (file != null && file.exists() && !file.delete()) {
            log.warn("Fail to delete file : {}", file.getAbsolutePath());
        }
    }

    /**
     * 判断资源是否为本地文件
     */
    public static boolean isFileResource(Resource resource) {
        if (resource == null) {
            return false;
        }
        try {
            resource.getFile();
            return true;
        } catch (IOException | IllegalStateException e) {
            return false;
        }
    }

    /**
     * 将资源物化为本地文件
     * 1. 本地文件资源直接返回原文件不负责删除
     * 2. 其他来源复制到临时文件后续调用方负责清理
     */
    public static File materialize(Resource resource) throws IOException {
        if (resource == null) {
            throw new IOException("resource is null");
        }
        try {
            return resource.getFile();
        } catch (IOException | IllegalStateException e) {
            File temp = File.createTempFile("parser_source_", ".bin");
            try (InputStream in = resource.getInputStream()) {
                FileUtil.writeFromStream(in, temp);
            } catch (IOException ex) {
                FileUtil.del(temp);
                throw ex;
            }
            log.debug("resource materialized to temp file: {}", temp.getAbsolutePath());
            return temp;
        }
    }

    /**
     * 根据检测文件类型
     */
    public static MediaTypeCategoryEnum detectByExtension(String filename) {
        String ext = getExtension(filename);
        if (StringUtils.isNotEmpty(ext)) {
            String normalized = ext.toLowerCase(Locale.ROOT);
            if (MediaTypeCategoryEnum.DOCUMENT.getExtensions().contains(normalized)) {
                return MediaTypeCategoryEnum.DOCUMENT;
            }
            if (MediaTypeCategoryEnum.MEDIA.getExtensions().contains(normalized)) {
                return MediaTypeCategoryEnum.MEDIA;
            }
            if (MediaTypeCategoryEnum.IMAGE.getExtensions().contains(normalized)) {
                return MediaTypeCategoryEnum.IMAGE;
            }
        }
        return null;
    }

    /**
     * 根据Content-Type检测文件类型
     */
    public static MediaTypeCategoryEnum detectByContentType(String contentType) {
        if (StringUtils.isNotEmpty(contentType)) {
            String type = contentType.toLowerCase(Locale.ROOT);

            if (type.startsWith("text/")) {
                return MediaTypeCategoryEnum.DOCUMENT;
            }
            if (type.startsWith("video/")) {
                return MediaTypeCategoryEnum.MEDIA;
            }
            if (type.startsWith("audio/")) {
                return MediaTypeCategoryEnum.MEDIA;
            }
            if (type.startsWith("image/")) {
                return MediaTypeCategoryEnum.IMAGE;
            }
            if (type.equals("application/pdf")
                || type.equals("application/rtf")
                || type.equals("application/xml")
                || type.equals("application/json")
                || type.equals("application/xhtml+xml")
                || type.equals("application/epub+zip")
                || type.startsWith("application/msword")
                || type.startsWith("application/vnd.ms-")
                || type.startsWith("application/vnd.openxmlformats-officedocument.")) {
                return MediaTypeCategoryEnum.DOCUMENT;
            }
        }
        return null;
    }

    public static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    public static String getExtension(final File file) {
        return FilenameUtils.getExtension(file.getName());
    }

    public static String getExtension(final String filename) {
        if (StringUtils.isNotEmpty(filename)) {
            return FilenameUtils.getExtension(filename);
        }
        return null;
    }

    public static long getFileSize(final File file) {
        return org.apache.commons.io.FileUtils.sizeOf(file);
    }

    public static String getContentType(final File file) {
        return getContentType(file.getName());
    }

    public static String getContentType(final String filename) {
        return FILE_NAME_MAP.getContentTypeFor(filename);
    }

    /**
     * 新建本地临时文件夹
     */
    public static File newTempFolder() {
        File tmpFile = new File(getTempDirectoryPath(), StringUtils.simpleUuid());
        if (tmpFile.exists()) {
            try {
                forceDelete(tmpFile);
            } catch (IOException e) {
                log.error("file already exists, but delete failed!", e);
            }
        }
        if (tmpFile.mkdirs()) {
            return tmpFile;
        }
        return null;
    }

    /**
     * 新建本地临时文件
     */
    public static File newTempFile(String filename) throws Exception {
        File tmpFile = new File(getTempDirectoryPath(), filename);
        // 强制建立目录，避免目录不存在报错
        forceMkdirParent(tmpFile);
        // 临时文件如果已经存在，强制删除，重新创建文件
        if (tmpFile.exists()) {
            try {
                forceDelete(tmpFile);
            } catch (IOException e) {
                log.error("Fail to create temp file [{}].", filename, e);
            }
        }
        if (tmpFile.createNewFile()) {
            log.info("create new file {}.", tmpFile.getAbsolutePath());
        }
        return tmpFile;
    }

}
