package cc.wdev.platform.commons.core.storage.oss;

import cc.wdev.platform.commons.core.storage.model.FileObject;
import cc.wdev.platform.commons.core.storage.model.FileUploadResult;
import cc.wdev.platform.commons.enums.StorageTypeEnum;
import com.aliyun.oss.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @author elvea
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OssFileObject implements FileObject<GenericResult> {

    @Builder.Default
    private StorageTypeEnum type = StorageTypeEnum.OSS;

    private String key;

    private File object;

    private String url;

    private FileUploadResult result;

    private GenericResult response;

}
