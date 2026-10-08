package cc.wdev.platform.commons.extensions.http.executor.okhttp;

import cc.wdev.platform.commons.extensions.http.HttpConfig;
import cc.wdev.platform.commons.extensions.http.HttpFactory;
import cc.wdev.platform.commons.extensions.http.enums.HttpRequestTypeEnum;
import cc.wdev.platform.commons.extensions.http.executor.HttpPostRequestExecutor;
import cc.wdev.platform.commons.utils.http.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class OkHttpPostRequestExecutor extends HttpPostRequestExecutor {

    public OkHttpPostRequestExecutor(HttpConfig config) {
        super(config);
    }

    @Override
    public String execute(HttpRequestTypeEnum type, String uri, Map<String, String> paramMap, Map<String, String> headerMap) throws Exception {
        log.info("OkHttp post execute - [{}] - [{}] - start", type, uri);
        OkHttpClient client = getHttpClient();
        Request.Builder builder = new Request.Builder().url(uri);
        OkHttpUtils.setRequestHeader(builder, headerMap);
        OkHttpUtils.setRequestJson(builder, paramMap);
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            String content = response.body().string();
            if (this.config.getDebug().isEnabled()) {
                log.info("OkHttp post execute - [{}] - [{}] - response - [{}]", type, uri, content);
            }
            return this.handleResponse(content);
        } finally {
            log.info("OkHttp post execute - [{}] - [{}] - finish", type, uri);
        }
    }

    protected OkHttpClient getHttpClient() {
        return HttpFactory.getOkHttpClient(this.config);
    }

}
