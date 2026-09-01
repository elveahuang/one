package cc.wdev.platform.system.catalog.domain.entity;

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
@TableName("sys_catalog")
public class CatalogEntity extends BaseTenantEntity {
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 业务ID
     */
    private Long bizId;
    /**
     * 编号
     */
    private String code;
    /**
     * 标题
     */
    private String title;
    /**
     * 是否顶层组织
     */
    private Integer rootInd;
    /**
     * 描述
     */
    private String description;
}
