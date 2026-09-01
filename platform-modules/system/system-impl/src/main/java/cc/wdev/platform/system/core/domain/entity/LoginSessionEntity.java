package cc.wdev.platform.system.core.domain.entity;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_login_session")
@Schema(description = "用户登录会话实体")
public class LoginSessionEntity extends BaseTenantEntity {
    /**
     * 用户ID
     */
    @Schema(description = "实体ID")
    private Long userId;
    /**
     * 会话标识
     */
    @Schema(description = "会话标识")
    private String sessionId;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    /**
     * 登录主机
     */
    @Schema(description = "登录主机")
    private String host;
    /**
     * User Agent
     */
    @Schema(description = "User Agent")
    private String ua;
    /**
     * 客户端编号
     */
    @Schema(description = "客户端编号")
    private String clientId;
    /**
     * 客户端名称
     */
    @Schema(description = "客户端名称")
    private String clientName;
    /**
     * 客户端版本
     */
    @Schema(description = "客户端版本")
    private String clientVersion;
    /**
     * 会话开始时间
     */
    @Schema(description = "会话开始时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime startDatetime;
    @Schema(description = "会话开始年")
    private Integer startYear;
    @Schema(description = "会话开始月")
    private Integer startMonth;
    @Schema(description = "会话开始日")
    private Integer startDay;
    @Schema(description = "会话开始时")
    private Integer startHour;
    @Schema(description = "会话开始分")
    private Integer startMinute;

    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime lastAccessDatetime;
    @Schema(description = "会话最后访问年")
    private Integer lastAccessYear;
    @Schema(description = "会话最后访问月")
    private Integer lastAccessMonth;
    @Schema(description = "会话最后访问日")
    private Integer lastAccessDay;
    @Schema(description = "会话最后访问时")
    private Integer lastAccessHour;
    @Schema(description = "会话最后访问分")
    private Integer lastAccessMinute;
    /**
     * 会话结束时间
     */
    @Schema(description = "会话结束时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime endDatetime;
    /**
     * 是否成功登录
     */
    @Schema(description = "是否成功登录")
    private Integer success;
}
