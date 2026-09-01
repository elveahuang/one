package cc.wdev.platform.commons.oapis.dingtalk.token;

import cc.wdev.platform.commons.oapis.dingtalk.cache.Cache;
import cc.wdev.platform.commons.oapis.dingtalk.config.AppConfig;
import cc.wdev.platform.commons.utils.StringUtils;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;

import java.util.concurrent.TimeUnit;

/**
 * @author elvea
 */
public class TokenManager {

    private static final long expiryDeltaOfSecond = 3 * 60;

    private static final String ACCESS_TOKEN_PREFIX = "dingtalk_access_token";

    private final Cache cache;

    private final String cacheKeyPrefix;

    public TokenManager(Cache cache) {
        this(cache, "");
    }

    public TokenManager(Cache cache, String cacheKeyPrefix) {
        this.cache = cache;
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    private String getAccessTokenKey(String appId) {
        String prefix = StringUtils.isNotEmpty(cacheKeyPrefix) ? (StringUtils.endsWithIgnoreCase(cacheKeyPrefix, ":") ? cacheKeyPrefix : (cacheKeyPrefix + ":")) : "";
        return prefix.concat(ACCESS_TOKEN_PREFIX).concat("-").concat(appId);
    }

    public String getAccessToken(AppConfig config) throws Exception {
        // 缓存里存在则直接返回
        String token = cache.get(getAccessTokenKey(config.getAppKey()));
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }
        // 发送请求获取访问凭证
        GetAccessTokenResponse response = get(config);
        token = response.getBody().getAccessToken();
        // 缓存
        cache.set(getAccessTokenKey(config.getAppKey()), token, response.getBody().getExpireIn() - expiryDeltaOfSecond, TimeUnit.SECONDS);
        return token;
    }

    private GetAccessTokenResponse get(AppConfig appConfig) throws Exception {
        Client client = new Client(appConfig.getConfig());
        GetAccessTokenRequest request = new GetAccessTokenRequest()
            .setAppKey(appConfig.getAppKey())
            .setAppSecret(appConfig.getAppSecret());
        GetAccessTokenResponse response = client.getAccessToken(request);
        if (response.getStatusCode() != 200) {
            throw new Exception("obtain internal app access token failure");
        }
        return response;
    }

}
