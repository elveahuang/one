package cc.wdev.platform.commons.oapis.dingtalk.token;


import cc.wdev.platform.commons.oapis.dingtalk.GlobalTokenManager;
import cc.wdev.platform.commons.oapis.dingtalk.cache.Cache;
import cc.wdev.platform.commons.oapis.dingtalk.config.AppConfig;
import cc.wdev.platform.commons.utils.StringUtils;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.CreateJsapiTicketHeaders;
import com.aliyun.dingtalkoauth2_1_0.models.CreateJsapiTicketResponse;
import com.aliyun.teautil.models.RuntimeOptions;

import java.util.concurrent.TimeUnit;

/**
 * @author elvea
 */
public class TicketManager {

    private static final long expiryDeltaOfSecond = 3 * 60;

    private static final String JSAPI_TICKET_PREFIX = "dingtalk_jsapi_ticket";

    private final Cache cache;

    private final String cacheKeyPrefix;

    public TicketManager(Cache cache) {
        this(cache, "");
    }

    public TicketManager(Cache cache, String cacheKeyPrefix) {
        this.cache = cache;
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    private String getJsapiTicketKey(String appId) {
        String prefix = StringUtils.isNotEmpty(cacheKeyPrefix) ? (StringUtils.endsWithIgnoreCase(cacheKeyPrefix, ":") ? cacheKeyPrefix : (cacheKeyPrefix + ":")) : "";
        return prefix.concat(JSAPI_TICKET_PREFIX).concat("-").concat(appId);
    }

    public String getJsapiTicket(AppConfig config) throws Exception {
        // 缓存存在钉钉临时凭证直接返回
        String ticket = cache.get(getJsapiTicketKey(config.getAppKey()));
        if (StringUtils.isNotEmpty(ticket)) {
            return ticket;
        }
        // 缓存不存在钉钉临时凭证则重新发送请求获取钉钉临时凭证
        CreateJsapiTicketResponse response = get(config);
        ticket = response.getBody().getJsapiTicket();
        cache.set(getJsapiTicketKey(config.getAppKey()), ticket, response.getBody().getExpireIn() - expiryDeltaOfSecond, TimeUnit.SECONDS);
        return ticket;
    }

    private CreateJsapiTicketResponse get(AppConfig config) throws Exception {
        Client client = new Client(config.getConfig());
        CreateJsapiTicketHeaders createJsapiTicketHeaders = new CreateJsapiTicketHeaders();
        createJsapiTicketHeaders.xAcsDingtalkAccessToken = GlobalTokenManager.getTokenManager().getAccessToken(config);
        CreateJsapiTicketResponse response = client.createJsapiTicketWithOptions(createJsapiTicketHeaders, new RuntimeOptions());
        if (response.getStatusCode() != 200) {
            throw new Exception("obtain internal app access token failure");
        }
        return response;
    }

}
