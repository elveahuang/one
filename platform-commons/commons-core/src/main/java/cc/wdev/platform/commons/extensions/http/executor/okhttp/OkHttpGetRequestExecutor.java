package cc.wdev.platform.commons.extensions.http.executor.okhttp;

import cc.wdev.platform.commons.extensions.http.HttpConfig;
import cc.wdev.platform.commons.extensions.http.HttpFactory;
import cc.wdev.platform.commons.extensions.http.enums.HttpRequestTypeEnum;
import cc.wdev.platform.commons.extensions.http.executor.HttpGetRequestExecutor;
import cc.wdev.platform.commons.utils.http.HttpUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class OkHttpGetRequestExecutor extends HttpGetRequestExecutor {

    public OkHttpGetRequestExecutor(HttpConfig config) {
        super(config);
    }

    @Override
    public String execute(HttpRequestTypeEnum type, String uri, Map<String, String> paramMap, Map<String, String> headerMap) throws IOException {
        final String url = HttpUtils.handleQueryParam(uri, paramMap);
        log.info("OkHttp execute - [{}] - [{}] - start", type, uri);
        OkHttpClient client = getHttpClient();
        Headers headers = MapUtils.isNotEmpty(headerMap) ? Headers.of(headerMap) : Headers.of(Maps.newHashMap());
        Request request = new Request.Builder().url(url).headers(headers).build();
        try (Response response = client.newCall(request).execute()) {
            String content = response.body().string();
            if (this.config.getDebug().isEnabled()) {
                log.info("OkHttp execute - [{}] - response - [{}]", url, content);
            }
            return this.handleResponse(content);
        } finally {
            log.info("OkHttp execute - [{}] - finish", url);
        }
    }

    protected OkHttpClient getHttpClient() {
        return HttpFactory.getOkHttpClient(this.config);
    }

}
