package cc.wdev.platform.system.catalog.domain.vo;

import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "目录对象")
public class CatalogVo {
    /**
     * 主键
     */
    private Long id;
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
    /**
     * 关联关系
     */
    private RelationVo<?> relation;
}
