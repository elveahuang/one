package cc.wdev.platform.commons.core.storage.aws;

import lombok.Data;
import software.amazon.awssdk.regions.Region;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
public class AwsStorageConfig implements Serializable {
    /**
     * 是否启用
     */
    private boolean enabled = false;
    /**
     * 附加参数
     * 需要根据实际服务商进行中配置
     */
    private boolean pathStyleEnabled = false;
    private boolean chunkedEncodingEnabled = false;
    private boolean crossRegionAccessEnabled = false;
    /**
     * Endpoint
     */
    private String endpoint = "";
    /**
     * Access Key
     */
    private String accessKey = "";
    /**
     * Secret Key
     */
    private String secretKey = "";
    /**
     * Region
     */
    private String region = Region.AWS_CN_GLOBAL.toString();
    /**
     * Bucket
     */
    private String bucket = "";
    /**
     * 域名
     */
    private String domain = "";
}
