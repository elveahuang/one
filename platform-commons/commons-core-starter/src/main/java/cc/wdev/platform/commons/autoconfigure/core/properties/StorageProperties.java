package cc.wdev.platform.commons.autoconfigure.core.properties;

import cc.wdev.platform.commons.core.storage.aws.AwsStorageConfig;
import cc.wdev.platform.commons.core.storage.oss.OssStorageConfig;
import cc.wdev.platform.commons.enums.StorageTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = StorageProperties.PREFIX)
public class StorageProperties {

    public static final String PREFIX = "platform.storage";

    private boolean enabled = false;

    private StorageTypeEnum type = StorageTypeEnum.AWS;

    @NestedConfigurationProperty
    private AwsStorageConfig aws = new AwsStorageConfig();

    @NestedConfigurationProperty
    private OssStorageConfig oss = new OssStorageConfig();

}
