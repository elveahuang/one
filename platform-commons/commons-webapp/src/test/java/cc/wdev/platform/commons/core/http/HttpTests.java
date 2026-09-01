package cc.wdev.platform.commons.core.http;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.extensions.http.HttpConfig;
import cc.wdev.platform.commons.extensions.http.HttpManger;
import cc.wdev.platform.commons.extensions.http.enums.HttpClientTypeEnum;
import cc.wdev.platform.commons.extensions.http.executor.HttpExecutor;
import cc.wdev.platform.commons.extensions.http.executor.HttpGetRequestExecutor;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class HttpTests extends BaseTests {

    @Test
    public void baseTest() throws Exception {
        HttpExecutor<String, Map<String, String>> executor = HttpGetRequestExecutor.create(HttpManger.getHttp().getConfig());
        executor.execute("https://www.coingecko.com/zh", Maps.newHashMap(), response -> {
            log.info("google response: {}", response);
        });
    }

    @Test
    public void baseOkHttpTest() throws Exception {
        HttpConfig config = HttpConfig.builder().type(HttpClientTypeEnum.OKHTTP).build();
        HttpExecutor<String, Map<String, String>> executor = HttpGetRequestExecutor.create(config);
        executor.execute("https://baidu.com", Maps.newHashMap(), response -> {
            log.info("okhttp response: {}", response);
        });
    }

    @Test
    public void baseApacheHttpTest() throws Exception {
        HttpConfig config = HttpConfig.builder().type(HttpClientTypeEnum.APACHE).build();
        HttpExecutor<String, Map<String, String>> executor = HttpGetRequestExecutor.create(config);
        executor.execute("https://baidu.com", Maps.newHashMap(), response -> {
            log.info("apache response: {}", response);
        });
    }

}
