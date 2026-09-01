package cc.wdev.platform.commons.core.tenant;

import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.NumberUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import static cc.wdev.platform.commons.constants.GlobalConstants.TENANT_ID_KEY;
import static cc.wdev.platform.commons.constants.GlobalConstants.TENANT_ROOT_IND_KEY;

/**
 * @author elvea
 */
@Slf4j
public class TenantContext {

    private final static String TENANT_ID = "tenantId";

    private final static String TENANT_ROOT_IND = "tenantRootInd";

    private static final ThreadLocal<Long> tenantId = new ThreadLocal<>();

    private static final ThreadLocal<Integer> tenantRootInd = new ThreadLocal<>();

    public static void handleServletRequest(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        String tenantId = request.getHeader(TENANT_ID_KEY);
        String tenantRootInd = request.getHeader(TENANT_ROOT_IND_KEY);
        if (StringUtils.isEmpty(tenantId) || StringUtils.isBlank(tenantRootInd)) {
            Tenant tenant = GlobalTenantManager.getResolver().resolveTenant(request);
            tenantId = tenant.getIdAsString();
            setTenantId(tenant.getId());
            tenantRootInd = NumberUtils.toString(tenant.getRootInd());
        }
        log.info("[TenantContext] with servlet tenantId [{}] for request [{}] [{}]", tenantId, request.getServerName(), request.getRequestURI());

        setTenantId(NumberUtils.toLong(tenantId));
        setTenantRootInd(NumberUtils.toInteger(tenantRootInd));

        if (!response.getHeaderNames().contains(TENANT_ID_KEY) || !response.getHeaderNames().contains(TENANT_ROOT_IND_KEY)) {
            response.setHeader(TENANT_ID_KEY, tenantId);
            response.setHeader(TENANT_ROOT_IND_KEY, tenantRootInd);
        }
    }

    public static Mono<Void> handleReactiveRequest(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        log.info("[TenantContext] handleReactiveRequest start");
        String tenantId = request.getHeaders().getFirst(TENANT_ID_KEY);

        if (StringUtils.isEmpty(tenantId)) {
            // 异步获取并设置 Context
            return GlobalTenantManager.getResolver().resolveTenant(request)
                .doOnNext(tenant -> {
                    setTenantId(tenant.getId());
                    setTenantRootInd(tenant.getRootInd());
                    log.info("[TenantContext] handleReactiveRequest end. resolved tenant [{}]", tenant.getId());
                })
                .then();
        } else {
            log.info("[TenantContext] handleReactiveRequest end. exist tenant [{}]", tenantId);
            return Mono.empty();
        }
    }

    public static void setTenantId(Long tenantId) {
        MDC.put(TENANT_ID, String.valueOf(tenantId));
    }

    public static Long getTenantId() {
        if (StringUtils.isEmpty(MDC.get(TENANT_ID))) {
            return GlobalTenantManager.getStore().root().getId();
        }
        return Long.valueOf(MDC.get(TENANT_ID));
    }

    public static String getTenantIdAsString() {
        if (StringUtils.isEmpty(MDC.get(TENANT_ID))) {
            return String.valueOf(GlobalTenantManager.getStore().root().getId());
        }
        return MDC.get(TENANT_ID);
    }

    public static void setTenantRootInd(Integer tenantRootInd) {
        MDC.put(TENANT_ROOT_IND, String.valueOf(tenantRootInd));
    }

    public static Integer getTenantRootInd() {
        if (StringUtils.isEmpty(MDC.get(TENANT_ROOT_IND))) {
            return GlobalTenantManager.getStore().root().getRootInd();
        }
        return Integer.valueOf(MDC.get(TENANT_ROOT_IND));
    }

    public static boolean isRootTenant() {
        if (StringUtils.isNotEmpty(MDC.get(TENANT_ROOT_IND))) {
            return BooleanTypeEnum.isTrueValue(getTenantRootInd());
        }
        return false;
    }

    public static void clear() {
        MDC.remove(TENANT_ID);
        MDC.remove(TENANT_ROOT_IND);
    }

}
