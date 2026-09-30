package cc.wdev.platform.commons.extensions.http.executor.apache;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.extensions.http.HttpConfig;
import cc.wdev.platform.commons.extensions.http.HttpFactory;
import cc.wdev.platform.commons.extensions.http.enums.HttpRequestTypeEnum;
import cc.wdev.platform.commons.extensions.http.executor.HttpGetRequestExecutor;
import cc.wdev.platform.commons.utils.http.ApacheHttpUtils;
import cc.wdev.platform.commons.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class ApacheGetRequestExecutor extends HttpGetRequestExecutor {

    public ApacheGetRequestExecutor(HttpConfig config) {
        super(config);
    }

    @Override
    public String execute(HttpRequestTypeEnum type, final String uri, Map<String, String> paramMap, Map<String, String> headerMap) throws IOException {
        final String url = HttpUtils.handleQueryParam(uri, paramMap);
        log.info("Apache execute - [{}] - [{}] - start", type, uri);
        HttpGet httpGet = new HttpGet(url);
        ApacheHttpUtils.setRequestParams(httpGet, headerMap);
        return getHttpClient().execute(httpGet, response -> {
            String content = "";
            if (!ObjectUtils.isEmpty(response) && !ObjectUtils.isEmpty(response.getEntity())) {
                content = EntityUtils.toString(response.getEntity(), GlobalConstants.UTF8);
                if (this.config.getDebug().isEnabled()) {
                    log.info("Apache execute - [{}] - [{}] - response - [{}]", type, url, content);
                }
            }
            log.info("Apache execute - [{}] - [{}] - finish", type, uri);
            return content;
        });
    }

    protected CloseableHttpClient getHttpClient() {
        return HttpFactory.getApacheHttpClient(this.config);
    }

}
