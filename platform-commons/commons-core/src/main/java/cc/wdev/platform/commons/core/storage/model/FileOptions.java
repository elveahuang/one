package cc.wdev.platform.commons.core.storage.model;

import cc.wdev.platform.commons.enums.StorageAccessTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件参数
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileOptions implements Serializable {

    /**
     * 媒体类型
     */
    private String contentType;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 目标文件名
     */
    private String filename;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 存储路径前嘴
     */
    private String prefix;

    /**
     * 存储路径
     */
    private String path;

    /**
     * 存储标识
     */
    private String key;

    /**
     * 访问类型
     */
    private StorageAccessTypeEnum accessType;

    public static FileOptions withPublic() {
        return FileOptions.builder().accessType(StorageAccessTypeEnum.PUBLIC).build();
    }

    public static FileOptions withPrivate() {
        return FileOptions.builder().accessType(StorageAccessTypeEnum.PRIVATE).build();
    }

    public static FileOptions withDefault() {
        return FileOptions.withPublic();
    }

}
