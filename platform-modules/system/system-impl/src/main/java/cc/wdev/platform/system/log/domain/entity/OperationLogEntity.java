package cc.wdev.platform.system.log.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleTenantEntity;
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
@TableName("sys_operation_log")
@Schema(description = "操作日志实体")
public class OperationLogEntity extends SimpleTenantEntity {
    /**
     * 租户ID"
     */
    @Schema(description = "租户ID")
    private Long tenantId;
    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private Long bizId;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 类名
     */
    @Schema(description = "类名")
    private String className;
    /**
     * 方法名
     */
    @Schema(description = "方法名")
    private String methodName;
    /**
     * 请求ID
     */
    @Schema(description = "请求ID")
    private String requestId;
    /**
     * 请求IP
     */
    @Schema(description = "请求IP")
    private String requestIp;
    /**
     * 请求UA
     */
    @Schema(description = "请求UA")
    private String requestUa;
    /**
     * 请求URI
     */
    @Schema(description = "请求URI")
    private String requestUri;
    /**
     * 请求方法
     */
    @Schema(description = "请求方法")
    private String requestMethod;
    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    private String requestParams;
    /**
     * 请求头
     */
    @Schema(description = "请求头")
    private String requestHeaders;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    /**
     * 执行时间
     */
    @Schema(description = "执行时间")
    private Long execTime;
    /**
     * 详情
     */
    @Schema(description = "详情")
    private String details;
    /**
     * 异常信息
     */
    @Schema(description = "异常信息")
    private String exception;
}
