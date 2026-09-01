package cc.wdev.platform.system.region.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "地址保存表单")
public class AddressForm implements Serializable {
    /**
     * 地址ID
     */
    @Schema(description = "地址ID")
    private Long id;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private String bizId;
    /**
     * 国家ID
     */
    @Schema(description = "国家ID")
    private Long countryId;
    /**
     * 省份ID
     */
    @Schema(description = "省份ID")
    private Long provinceId;
    /**
     * 城市ID
     */
    @Schema(description = "城市ID")
    private Long cityId;
    /**
     * 地址标题
     */
    @Schema(description = "地址标题")
    @NotBlank(message = "地址标题不能为空")
    private String title;

    /**
     * 工作地点
     */
    @Schema(description = "工作地点")
    @NotBlank(message = "工作地点不能为空")
    private String location;

    /**
     * 县区ID
     */
    @Schema(description = "县区ID")
    private Long countyId;
    /**
     * 地址详情
     */
    @Schema(description = "详细地址")
    @NotBlank(message = "详细地址不能为空")
    private String details;
    /**
     * 经度
     */
    @Schema(description = "经度")
    private String lng;
    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private String lat;
}
