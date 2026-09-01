package cc.wdev.platform.commons.core.log.aspect;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.log.config.LogConfig;
import cc.wdev.platform.commons.core.log.domain.OperationLogDto;
import cc.wdev.platform.commons.core.log.store.LogStore;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.utils.AopUtils;
import cc.wdev.platform.commons.utils.ExceptionUtils;
import cc.wdev.platform.commons.utils.WebServletUtils;
import cc.wdev.platform.commons.utils.mdc.MdcContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author elvea
 */
@Slf4j
@Aspect
public record OperationLogAspect(LogStore store, LogConfig config) {

    private static final ThreadLocal<StopWatch> stopWatchHolder = new ThreadLocal<>();

    /**
     * 执行前
     */
    @Before(value = "@annotation(operationLog)")
    public void boBefore(JoinPoint point, OperationLog operationLog) {
        stopWatchHolder.set(StopWatch.createStarted());
        MdcContext.handleAspect();
    }

    /**
     * 执行成功并返回
     */
    @AfterReturning(pointcut = "@annotation(operationLog)", returning = "result")
    public void doAfterReturning(JoinPoint point, OperationLog operationLog, Object result) {
        outputLog(point, operationLog, null, result);
    }

    /**
     * 执行失败捕捉异常
     */
    @AfterThrowing(value = "@annotation(operationLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog operationLog, Exception e) {
        outputLog(joinPoint, operationLog, e, null);
    }

    private void outputLog(JoinPoint joinPoint, OperationLog operationLog, Throwable e, Object result) {
        try {
            // 获取执行时间
            StopWatch stopWatch = stopWatchHolder.get();
            stopWatch.stop();

            HttpServletRequest request = WebServletUtils.getRequest();

            OperationLogDto dto = OperationLogDto.builder()
                .className(AopUtils.getJoinPointClass(joinPoint))
                .methodName(AopUtils.getJoinPointMethod(joinPoint))
                .tenantId(TenantContext.getTenantId())
                .requestUri(request.getRequestURI())
                .requestMethod(request.getMethod())
                .requestId(MdcContext.getRequestId())
                .requestIp(WebServletUtils.getHost(request))
                .requestUa(WebServletUtils.getUserAgent(request))
                .requestParams(WebServletUtils.getParameterAsJson(request))
                .requestHeaders(WebServletUtils.getHeaderAsJson(request))
                .exception(e != null ? ExceptionUtils.getStackTraceAsString(e) : "")
                .details(operationLog.value())
                .startTime(LocalDateTime.ofInstant(stopWatch.getStartInstant(), ZoneId.systemDefault()))
                .endTime(LocalDateTime.ofInstant(stopWatch.getStopInstant(), ZoneId.systemDefault()))
                .execTime(stopWatch.getNanoTime())
                .build();

            store.saveOperationLog(dto);
        } catch (Exception ex) {
            log.error("Failed to save operation log", ex);
        } finally {
            stopWatchHolder.remove();
        }
    }

}
