package cc.wdev.platform.system.tag.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author irving
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tag_relation")
public class TagRelationEntity extends SimpleTenantEntity {
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 业务ID
     */
    private Long bizId;
    /**
     * 标签ID
     */
    private Long tagId;
}
