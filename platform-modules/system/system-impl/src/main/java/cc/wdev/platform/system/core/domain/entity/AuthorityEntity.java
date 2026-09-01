package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import cc.wdev.platform.system.commons.enums.*;
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
@TableName("sys_authority")
public class AuthorityEntity extends BaseEntity {
    /**
     * Parent ID
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
     * @see AuthorityTypeEnum
     */
    private String authorityType;
    /**
     * @see PackageBizTypeEnum
     */
    private String authorityBizType;
    /**
     * @see BizScopeTypeEnum
     */
    private String authorityScopeType;
    /**
     * @see BaseRoleTypeEnum
     * @see RoleTypeEnum
     */
    private String authorityRoleType;
    /**
     * 说明
     */
    private String description;
    /**
     * 排序序号
     */
    private Integer idx;
}
