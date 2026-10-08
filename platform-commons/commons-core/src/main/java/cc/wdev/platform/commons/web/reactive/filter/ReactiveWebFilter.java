package cc.wdev.platform.commons.web.reactive.filter;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.utils.mdc.MdcContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static cc.wdev.platform.commons.constants.GlobalConstants.REQUEST_ID_KEY;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class ReactiveWebFilter implements WebFilter, Ordered {

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        log.info("[ReactiveWebFilter] doFilter Start");

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        try {
            // 初始化MDC上下文
            log.info("[ReactiveWebFilter] Init MDC Context");
            MdcContext.handleReactiveRequest(request, response);
            log.info("[ReactiveWebFilter] MDC Context {}. URL [{}]", MdcContext.getRequestId(), request.getPath());

            ServerHttpRequest wr = exchange.getRequest()
                .mutate()
                .header(REQUEST_ID_KEY, MdcContext.getRequestId())
                .build();

            final long startTime = System.currentTimeMillis();
            return chain.filter(exchange.mutate().request(wr).build()).doFinally(_ -> {
                final long endTime = System.currentTimeMillis();
                final long totalTime = endTime - startTime;

                log.info("[ReactiveWebFilter] doFilter end. URL [{}]. totalTime: [{}]", exchange.getRequest().getPath(), totalTime);
            });
        } finally {
            log.info("[ReactiveWebFilter] Clear Tenant Context");
            TenantContext.clear();

            log.info("[ReactiveWebFilter] Clear MDC Context");
            MdcContext.clear();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
