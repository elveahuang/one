package cc.wdev.platform.system.region.domain.vo;


import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "地址对象")
public class AddressVo implements Serializable {
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    @Schema(description = "业务类型")
    private String bizType;
    @Schema(description = "国家ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long countryId;
    @Schema(description = "省份ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long provinceId;
    @Schema(description = "城市ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cityId;
    @Schema(description = "县区ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long countyId;
    @Schema(description = "国家名称")
    private String countryName;
    @Schema(description = "省份名称")
    private String provinceName;
    @Schema(description = "城市名称")
    private String cityName;
    @Schema(description = "县区名称")
    private String countyName;
    @Schema(description = "地址标题")
    private String title;
    @Schema(description = "地点")
    private String location;
    @Schema(description = "地址详情")
    private String details;
    @Schema(description = "额外信息")
    private String extra;
    @Schema(description = "经度")
    private String lng;
    @Schema(description = "纬度")
    private String lat;
    /**
     * 工作地点距离Km
     */
    @Schema(description = "距离 单位（米）")
    private Double distance;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    /**
     * 获取完整地址文本
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(this.provinceName)) {
            sb.append(this.provinceName);
        }
        if (StringUtils.isNotBlank(this.cityName)) {
            sb.append(this.cityName);
        }
        if (StringUtils.isNotBlank(this.countyName)) {
            sb.append(this.countyName);
        }
        if (StringUtils.isNotBlank(this.details)) {
            sb.append(this.details);
        }
        return sb.toString();
    }

    /**
     * 获取完整地址文本（带长度限制）
     *
     * @param maxLength 最大长度
     * @return 截断后的地址文本
     */
    public String getFullAddress(int maxLength) {
        String fullAddress = getFullAddress();
        if (fullAddress.length() <= maxLength) {
            return fullAddress;
        }
        return fullAddress.substring(0, maxLength - 4) + "...";
    }
}
