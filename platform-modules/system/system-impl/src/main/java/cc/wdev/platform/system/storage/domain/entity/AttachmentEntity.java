package cc.wdev.platform.system.storage.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_attachment")
public class AttachmentEntity extends BaseTenantEntity {
    /**
     * 附件业务类型
     */
    private String bizType;
    /**
     * 内容类型
     */
    private String contentType;
    /**
     * 存储类型
     */
    private String storageType;
    /**
     * 访问类型
     */
    private String accessType;
    /**
     * 原始文件名
     */
    private String originalFilename;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 文件标识
     */
    private String fileKey;
    /**
     * 文件链接
     */
    private String url;
    /**
     * 附加信息
     */
    private String extra;
    /**
     * 文件大小
     */
    private Long size;
}
