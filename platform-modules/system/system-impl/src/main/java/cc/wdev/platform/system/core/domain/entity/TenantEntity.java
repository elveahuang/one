package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author erden
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class TenantEntity extends BaseEntity implements CodeEntity {
    /**
     * 租户编号
     */
    private String code;
    /**
     * 名称
     */
    private String title;
    /**
     * 简介
     */
    private String details;
    /**
     * 地址
     */
    private String address;
    /**
     * 域名
     */
    private String domain;
    /**
     * 联系人
     */
    private String contactUser;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 统一社会信用代码
     */
    private String companyLicenseNumber;
    /**
     * 注册时间
     */
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime registrationDate;
    /**
     * 到期时间
     */
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime expirationDate;
    /**
     * 租户用户数
     */
    private Integer accountCount;
    /**
     * 是否顶层用户
     */
    private Integer rootInd;
    /**
     * 备注说明
     */
    private String description;
    /**
     * 来源
     */
    private Integer source;
    /**
     * 状态
     */
    private Integer status;
}
