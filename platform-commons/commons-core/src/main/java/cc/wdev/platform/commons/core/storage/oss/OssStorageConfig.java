package cc.wdev.platform.commons.core.storage.oss;

import lombok.Data;

import java.io.Serializable;

/**
 * 阿里云存储配置参数
 *
 * @author elvea
 */
@Data
public class OssStorageConfig implements Serializable {
    /**
     * 是否启用
     */
    private boolean enabled = false;
    /**
     * Endpoint
     */
    private String endpoint = "";
    /**
     * Access Key ID
     */
    private String accessKeyId = "";
    /**
     * Access Key Secret
     */
    private String accessKeySecret = "";
    /**
     * Bucket
     */
    private String bucket = "";
    /**
     * 区域
     */
    private String region = "";
    /**
     * 域名
     */
    private String domain = "";
}
