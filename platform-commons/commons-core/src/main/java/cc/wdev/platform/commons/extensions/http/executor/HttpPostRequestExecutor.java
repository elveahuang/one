package cc.wdev.platform.commons.extensions.http.executor;

import cc.wdev.platform.commons.extensions.http.HttpConfig;
import cc.wdev.platform.commons.extensions.http.enums.HttpRequestTypeEnum;
import cc.wdev.platform.commons.extensions.http.executor.apache.ApachePostRequestExecutor;
import cc.wdev.platform.commons.extensions.http.executor.okhttp.OkHttpPostRequestExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author elvea
 */
public abstract class HttpPostRequestExecutor implements HttpExecutor<String, Map<String, String>> {

    protected HttpConfig config;

    public HttpPostRequestExecutor(HttpConfig config) {
        this.config = config;
    }

    @Override
    public String execute(String uri, Map<String, String> paramMap, Map<String, String> headerMap) throws Exception {
        return this.execute(HttpRequestTypeEnum.POST, uri, paramMap, headerMap);
    }

    public static HttpExecutor<String, Map<String, String>> create(HttpConfig config) {
        return switch (config.getType()) {
            case APACHE -> new ApachePostRequestExecutor(config);
            case OKHTTP -> new OkHttpPostRequestExecutor(config);
        };
    }

    @NotNull
    public String handleResponse(String response) {
        return response;
    }

}
