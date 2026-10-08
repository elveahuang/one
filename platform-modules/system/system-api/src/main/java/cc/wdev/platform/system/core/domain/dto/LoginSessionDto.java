package cc.wdev.platform.system.core.domain.dto;


import cc.wdev.platform.commons.enums.ActionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录会话DTO")
public class LoginSessionDto implements Serializable {
    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private ActionTypeEnum actionType;
    /**
     * 会话标识
     */
    @Schema(description = "会话标识")
    private String sessionId;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
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
    private LocalDateTime startDatetime;
    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    private LocalDateTime lastAccessDatetime;
    /**
     * 会话结束时间
     */
    @Schema(description = "会话结束时间")
    private LocalDateTime endDatetime;
    /**
     * 是否成功登录
     */
    @Schema(description = "是否成功登录")
    private Integer success;
}
