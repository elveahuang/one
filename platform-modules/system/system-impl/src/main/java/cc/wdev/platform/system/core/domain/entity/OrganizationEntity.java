package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 部门实体
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_organization")
public class OrganizationEntity extends BaseTenantEntity {
    /**
     * 父级部门ID
     */
    private Long parentId;
    /**
     * 编号
     */
    private String code;
    /**
     * 标题
     */
    private String title;
    /**
     * 文本
     */
    private String label;
    /**
     * 描述
     */
    private String description;
    /**
     * 是否顶层部门
     */
    private Integer rootInd;
    /**
     * 是否默认部门
     */
    private Integer defaultInd;
    /**
     * 状态
     */
    private Integer status;
}
