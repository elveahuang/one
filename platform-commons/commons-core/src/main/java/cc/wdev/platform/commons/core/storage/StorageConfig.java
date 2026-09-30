package cc.wdev.platform.commons.core.storage;

import cc.wdev.platform.commons.core.storage.aws.AwsStorageConfig;
import cc.wdev.platform.commons.core.storage.oss.OssStorageConfig;
import cc.wdev.platform.commons.enums.StorageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageConfig implements Serializable {

    @Builder.Default
    private StorageTypeEnum type = StorageTypeEnum.AWS;

    @Builder.Default
    private AwsStorageConfig aws = new AwsStorageConfig();

    @Builder.Default
    private OssStorageConfig oss = new OssStorageConfig();

}
