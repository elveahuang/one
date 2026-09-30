package cc.wdev.platform.system.storage.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleTenantEntity;
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
@TableName("sys_attachment_relation")
public class AttachmentRelationEntity extends SimpleTenantEntity {
    /**
     * 目标业务类型
     */
    private String bizType;
    /**
     * 目标业务ID
     */
    private Long bizId;
    /**
     * 文件ID
     */
    private Long attachmentId;
}
