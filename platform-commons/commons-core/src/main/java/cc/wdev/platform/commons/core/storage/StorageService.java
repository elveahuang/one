package cc.wdev.platform.commons.core.storage;

import cc.wdev.platform.commons.core.storage.model.FileObject;
import cc.wdev.platform.commons.core.storage.model.FileOptions;
import cc.wdev.platform.commons.core.storage.model.GenerateUrlRequest;
import cc.wdev.platform.commons.utils.FileUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collection;
import java.util.Map;

/**
 * 存储服务
 *
 * @author elvea
 */
public interface StorageService<C> {

    /**
     * 获取客户端
     */
    C getClient();

    /**
     * 关闭客户端
     */
    void closeClient(C client);

    /**
     * 获取存储桶名称
     */
    String getBucket();

    /**
     * 自定义域名
     */
    String getEndpoint();

    /**
     * 自定义域名
     */
    String getDomain();

    /**
     * 获取文件链接
     */
    default FileObject<?> getUrl(String key) {
        return this.getUrl(GenerateUrlRequest.builder().key(key).build());
    }

    /**
     * 获取文件链接
     */
    FileObject<?> getUrl(GenerateUrlRequest request);

    /**
     * 获取文件信息
     */
    FileObject<?> getFile(String key);

    /**
     * 上传文件
     */
    default FileObject<?> uploadFile(MultipartFile file) throws Exception {
        FileOptions options = FileOptions.builder()
            .originalFilename(file.getOriginalFilename())
            .contentType(file.getContentType())
            .size(file.getSize())
            .build();
        return this.uploadFile(file, options);
    }

    /**
     * 上传文件
     */
    default FileObject<?> uploadFile(MultipartFile file, FileOptions options) throws Exception {
        // 处理上传参数 -- 原始文件名
        if (StringUtils.isEmpty(options.getOriginalFilename())) {
            options.setOriginalFilename(file.getOriginalFilename());
        }
        // 处理上传参数 -- 文件类型
        if (StringUtils.isEmpty(options.getContentType())) {
            options.setContentType(file.getContentType());
        }
        // 处理上传参数 -- 文件大小
        if (options.getSize() <= 0) {
            options.setSize(file.getSize());
        }

        try (BufferedInputStream is = new BufferedInputStream(file.getInputStream())) {
            return this.uploadFile(is, options);
        }
    }

    /**
     * 上传文件
     */
    default FileObject<?> uploadFile(File file) throws Exception {
        return this.uploadFile(file, FileOptions.builder().build());
    }

    /**
     * 上传文件
     */
    default FileObject<?> uploadFile(File file, FileOptions options) throws Exception {
        // 处理上传参数 -- 原始文件名
        if (StringUtils.isEmpty(options.getOriginalFilename())) {
            options.setOriginalFilename(file.getName());
        }
        // 处理上传参数 -- 文件类型
        if (StringUtils.isEmpty(options.getContentType())) {
            options.setContentType(FileUtils.getContentType(file));
        }
        // 处理上传参数 -- 文件大小
        if (options.getSize() <= 0) {
            options.setSize(FileUtils.getFileSize(file));
        }

        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return this.uploadFile(is, options);
        }
    }

    /**
     * 上传文件
     */
    FileObject<?> uploadFile(InputStream is, FileOptions options) throws Exception;

    /**
     * 下载文件
     */
    default void download(String key, OutputStream out) {
    }

    /**
     * 获取文件presignedUrl
     */
    Map<String, String> presignedObjectUrlMap(Collection<String> pathList);

}
