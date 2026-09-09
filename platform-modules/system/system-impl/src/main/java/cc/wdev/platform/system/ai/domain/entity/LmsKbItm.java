package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 知识库关联表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_lms_kb_itm")
public class LmsKbItm extends SimpleTenantEntity {

    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 知识库ID
     */
    private Long kbId;
    /**
     * 类型
     */
    private String type;
    /**
     * 目标类型
     */
    private String targetType;
    /**
     * 课程ID
     */
    private Long targetId;
    /**
     * 目标资源类型
     */
    private String targetResType;
    /**
     * 资源ID
     */
    private Long targetResId;
    /**
     * 内容
     */
    private String content;

}
