package cc.wdev.platform.commons.core.storage;

import cc.wdev.platform.commons.core.storage.aws.AwsStorageConfig;
import cc.wdev.platform.commons.core.storage.aws.AwsStorageService;
import cc.wdev.platform.commons.core.storage.oss.OssStorageConfig;
import cc.wdev.platform.commons.core.storage.oss.OssStorageService;
import cc.wdev.platform.commons.enums.StorageTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public record StorageFactory(StorageConfig config) {

    public StorageService<?> getStorageService() {
        if (StorageTypeEnum.OSS.equals(this.config.getType())) {
            return getOssStorageService();
        }
        return getAwsStorageService();
    }

    public StorageConfig getConfig() {
        return this.config;
    }

    public AwsStorageService getAwsStorageService() {
        return this.getAwsStorageService(this.config.getAws());
    }

    public AwsStorageService getAwsStorageService(AwsStorageConfig config) {
        return new AwsStorageService(config);
    }

    public OssStorageService getOssStorageService() {
        return this.getOssStorageService(this.config.getOss());
    }

    public OssStorageService getOssStorageService(OssStorageConfig config) {
        return new OssStorageService(config);
    }

}
