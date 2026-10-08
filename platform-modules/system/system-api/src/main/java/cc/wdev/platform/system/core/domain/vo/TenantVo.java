package cc.wdev.platform.system.core.domain.vo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)

@Schema(description = "租户VO")
public class TenantVo implements Serializable {
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 租户编号
     */
    @Schema(description = "租户编号")
    private String code;
    /**
     * 名称
     */
    @Schema(description = "名称")
    private String title;
    /**
     * 简介
     */
    @Schema(description = "简介")
    private String details;
    /**
     * 地址
     */
    @Schema(description = "地址")
    private String address;
    /**
     * 域名
     */
    @Schema(description = "域名")
    private String domain;
    /**
     * 联系人
     */
    @Schema(description = "联系人")
    private String contactUser;
    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String contactPhone;
    /**
     * 企业名称
     */
    @Schema(description = "企业名称")
    private String companyName;
    /**
     * 统一社会信用代码
     */
    @Schema(description = "统一社会信用代码")
    private String companyLicenseNumber;
    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    private LocalDateTime registrationDate;
    /**
     * 到期时间
     */
    @Schema(description = "到期时间")
    private LocalDateTime expirationDate;
    /**
     * 租户用户数
     */
    @Schema(description = "租户用户数")
    private Integer accountCount;
    /**
     * 是否顶层用户
     */
    @Schema(description = "是否顶层用户")
    private Integer rootInd;
    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String description;
    /**
     * 来源
     */
    @Schema(description = "来源")
    private Integer source;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 租户封面
     */
    @Schema(description = "租户封面")
    private AttachmentVo cover;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    /**
     * 关联的套餐ID数组
     */
    @Schema(description = "关联的套餐ID数组")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> packageIds;
}
