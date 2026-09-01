package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_package_item")
public class PackageItemEntity extends BaseEntity {
    /**
     * 业务类型
     */
    private Long bizType;
    /**
     * 套餐ID
     */
    private Long packageId;
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
     * 是否自动续费
     */
    private Integer automaticRenewalInd;
    /**
     * 划线价格
     */
    private BigDecimal listPrice;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 单位
     */
    private Integer dateUnit;
    /**
     * 单位
     */
    private Integer dateValue;
    /**
     * 序号
     */
    private Integer idx;
    /**
     * 备注
     */
    private String description;
    /**
     * 发布状态
     */
    private Integer status;
}
