package cc.wdev.platform.commons.core.storage.aws;

import cc.wdev.platform.commons.core.storage.model.FileObject;
import cc.wdev.platform.commons.core.storage.model.FileUploadResult;
import cc.wdev.platform.commons.enums.StorageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.S3Response;

import java.io.File;

/**
 * @author elvea
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AwsFileObject implements FileObject<S3Response> {

    @Builder.Default
    private StorageTypeEnum type = StorageTypeEnum.AWS;

    private String key;

    private String url;

    private String etag;

    private File object;

    private S3Response response;

    private FileUploadResult result;

}
