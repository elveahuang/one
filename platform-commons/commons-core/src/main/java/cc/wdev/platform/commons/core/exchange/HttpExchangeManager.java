package cc.wdev.platform.commons.core.exchange;

import cc.wdev.platform.commons.core.exchange.interceptor.MdcRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author elvea
 */
@Slf4j
public class HttpExchangeManager {

    private final RestClient.Builder restClientBuilder;

    public HttpExchangeManager(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public <T> T getHttpExchange(Class<T> clazz, String baseUrl) {
        // 构建客户端
        RestClient restClient = restClientBuilder.baseUrl(baseUrl)
            .requestInterceptor(new MdcRequestInterceptor())
            .build();
        return this.getHttpExchange(restClient, clazz);
    }

    public <T> T getHttpExchange(RestClient restClient, Class<T> clazz) {
        // 构建适配器
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        // 创建代理工厂
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        // 生成代理实现
        return factory.createClient(clazz);
    }

    public <T> T getHttpExchange(WebClient webClient, Class<T> clazz) {
        // 构建适配器
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        // 创建代理工厂
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        // 生成代理实现
        return factory.createClient(clazz);
    }

    /**
     * 创建默认管理器
     */
    public static HttpExchangeManager createHttpExchangeManager() {
        RestClient.Builder builder = RestClient.builder();
        return new HttpExchangeManager(builder);
    }

}
