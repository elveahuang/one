package cc.wdev.platform.commons.core.tenant;

import cc.wdev.platform.commons.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static cc.wdev.platform.commons.constants.GlobalConstants.TENANT_CODE_KEY;
import static cc.wdev.platform.commons.constants.GlobalConstants.TENANT_ID_KEY;

/**
 * @author elvea
 */
public record DefaultTenantResolver(TenantStore store, TenantConfig config) implements TenantResolver {

    /**
     * @see TenantResolver#resolveTenant(HttpServletRequest)
     */
    @Override
    public Tenant resolveTenant(HttpServletRequest request) {
        if (this.config.isEnabled()) {
            Tenant defaultTenant = getDefaultTenant();
            if (StringUtils.isNotEmpty(request.getHeader(TENANT_ID_KEY))) {
                return this.store.findById(Long.valueOf(request.getHeader(TENANT_ID_KEY)));
            } else if (StringUtils.isNotEmpty(request.getHeader(TENANT_CODE_KEY))) {
                return this.store.findByCode(request.getHeader(TENANT_CODE_KEY));
            }
            String serverName = request.getServerName();
            return this.store.findByDomain(serverName);
        } else {
            return this.store.root();
        }
    }

    /**
     * @see TenantResolver#resolveTenant(ServerHttpRequest)
     */
    @Override
    public Mono<Tenant> resolveTenant(ServerHttpRequest request) {
        return Mono.fromCallable(() -> {
            if (this.config.isEnabled()) {
                Tenant defaultTenant = getDefaultTenant();

                String tenantId = request.getHeaders().getFirst(TENANT_ID_KEY);
                if (StringUtils.isNotEmpty(tenantId)) {
                    return this.store.findById(Long.valueOf(tenantId));
                }

                String tenantCode = request.getHeaders().getFirst(TENANT_CODE_KEY);
                if (StringUtils.isNotEmpty(tenantCode)) {
                    return this.store.findByCode(tenantCode);
                }

                String domain = request.getURI().getHost();
                return this.store.findByDomain(domain);
            } else {
                return this.store.root();
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * @see TenantResolver#getDefaultTenant()
     */
    @Override
    public Tenant getDefaultTenant() {
        return this.store.root();
    }

}
