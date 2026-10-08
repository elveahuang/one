package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author elvea
 */
@Slf4j
public abstract class WebReactiveUtils {

    public static Mono<Void> renderJson(@NonNull ServerHttpResponse response, Object object) throws Exception {
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.getType());
        DataBuffer dataBuffer = response.bufferFactory().wrap(JacksonUtils.toBytes(object));
        return response.writeWith(Mono.just(dataBuffer));
    }

    /**
     * 判断请求是否是内部微服务请求
     */
    public static ServerWebExchangeMatcher Internal_Request_Matcher = new AndServerWebExchangeMatcher(
        new PathPatternParserServerWebExchangeMatcher("/actuator/**")
    );

    public static Mono<ServerWebExchangeMatcher.MatchResult> isInternalRequest(ServerWebExchange exchange) {
        return Internal_Request_Matcher.matches(exchange);
    }

}
