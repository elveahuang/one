package cc.wdev.platform.commons.core.exchange.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author elvea
 */
@Slf4j
public class LogRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public @NonNull ClientHttpResponse intercept(@NonNull HttpRequest request,
                                                 byte @NonNull [] body,
                                                 @NonNull ClientHttpRequestExecution execution) throws IOException {

        // 请求日志
        log.info("URI: {}, Method: {}, Headers: {}", request.getURI(), request.getMethod(), request.getHeaders());
        if (body.length > 0) {
            log.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
        }

        // 执行请求
        ClientHttpResponse response = execution.execute(request, body);

        // 响应日志
        log.info("Response Status: {}", response.getStatusCode());
        byte[] responseBody = response.getBody().readAllBytes();
        if (responseBody.length > 0) {
            log.info("Response Body: {}", new String(responseBody, StandardCharsets.UTF_8));
        }
        return response;
    }

}
