package cc.wdev.platform.system.ai.domain.entity;

import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_ai_tool")
public class AiToolEntity extends BaseTenantEntity implements CodeEntity {
    /**
     * 编号
     */
    private String code;
    /**
     * 标题
     */
    private String title;
    /**
     * 工具名称
     */
    private String toolName;
    /**
     * 备注说明
     */
    private String description;
    /**
     * 状态
     */
    private Integer status;
}
