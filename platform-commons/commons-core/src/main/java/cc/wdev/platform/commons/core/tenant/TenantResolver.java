package cc.wdev.platform.commons.core.tenant;

import cc.wdev.platform.commons.utils.WebServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 * @author elvea
 */
public interface TenantResolver {

    Tenant resolveTenant(HttpServletRequest request);

    Mono<Tenant> resolveTenant(ServerHttpRequest request);

    default Tenant resolveTenant() {
        return this.resolveTenant(WebServletUtils.getRequest());
    }

    default Tenant getDefaultTenant() {
        return Tenant.defaultTenant;
    }

}
