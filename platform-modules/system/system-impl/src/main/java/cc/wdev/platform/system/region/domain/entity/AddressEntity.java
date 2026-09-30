package cc.wdev.platform.system.region.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_address")
public class AddressEntity extends BaseTenantEntity {
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 业务ID
     */
    private Long bizId;
    /**
     * 国家ID
     */
    private Long countryId;
    /**
     * 省份ID
     */
    private Long provinceId;
    /**
     * 城市ID
     */
    private Long cityId;
    /**
     * 县区ID
     */
    private Long countyId;
    /**
     * 地点标题
     */
    private String title;
    /**
     * 地点
     */
    private String location;
    /**
     * 详细地址
     */
    private String details;
    /**
     * 附加信息
     */
    private String extra;
    /**
     * 经度
     */
    private String lng;
    /**
     * 纬度
     */
    private String lat;
    /**
     * 状态
     */
    private Integer status;

}
