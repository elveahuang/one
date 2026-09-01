package cc.wdev.platform.system.region.domain.entity;


import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("sys_region")
public class RegionEntity extends BaseEntity {
    /**
     * ID
     */
    private Long id;
    /**
     * 上级区域ID
     */
    private Long parentId;
    /**
     * 行政区划类型
     */
    private String type;
    /**
     * 行政区划代码
     */
    private String code;
    /**
     * 单位名称
     */
    private String title;
    /**
     * 单位名称拼音首字母大写
     */
    private String titleFirstLetter;
    /**
     * 是否有子节点（非数据库字段，用于前端级联选择器）
     */
    @TableField(exist = false)
    private Boolean hasChildren;
}
