package cc.wdev.platform.commons.core.exchange.interceptor;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.utils.NumberUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.utils.mdc.MdcContext;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static cc.wdev.platform.commons.constants.GlobalConstants.*;

/**
 * @author elvea
 */
@Slf4j
public class MdcRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public @NonNull ClientHttpResponse intercept(@NonNull HttpRequest request,
                                                 byte @NonNull [] body,
                                                 @NonNull ClientHttpRequestExecution execution) throws IOException {
        String tenantId = TenantContext.getTenantIdAsString();
        String tenantRootInd = NumberUtils.toString(TenantContext.getTenantRootInd());
        String requestId = MdcContext.getRequestId();
        if (StringUtils.isNotEmpty(requestId)) {
            request.getHeaders().add(REQUEST_ID_KEY, requestId);
        }
        if (StringUtils.isNotEmpty(tenantId) || StringUtils.isNotEmpty(tenantRootInd)) {
            request.getHeaders().add(TENANT_ID_KEY, tenantId);
            request.getHeaders().add(TENANT_ROOT_IND_KEY, tenantRootInd);
        }
        // 继续执行请求
        return execution.execute(request, body);
    }

}
