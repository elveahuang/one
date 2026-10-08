package cc.wdev.platform.commons.oapis.lark.token;

import cc.wdev.platform.commons.oapis.dingtalk.service.DingTalkService;
import cc.wdev.platform.commons.oapis.lark.cache.Cache;
import cc.wdev.platform.commons.utils.StringUtils;
import com.lark.oapi.core.Config;
import com.lark.oapi.core.Constants;
import com.lark.oapi.core.Transport;
import com.lark.oapi.core.enums.AppType;
import com.lark.oapi.core.exception.AppTicketIsEmptyException;
import com.lark.oapi.core.exception.ObtainAccessTokenException;
import com.lark.oapi.core.request.MarketplaceAppAccessTokenReq;
import com.lark.oapi.core.request.MarketplaceTenantAccessTokenReq;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.request.SelfBuiltAppAccessTokenReq;
import com.lark.oapi.core.response.AppAccessTokenResp;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.response.TenantAccessTokenResp;
import com.lark.oapi.core.token.AccessTokenType;
import com.lark.oapi.core.token.GlobalAppTicketManager;
import com.lark.oapi.core.utils.Sets;
import com.lark.oapi.core.utils.Strings;
import com.lark.oapi.core.utils.UnmarshalRespUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author elvea
 * @see DingTalkService
 */
@Slf4j
public class TokenManager extends com.lark.oapi.core.token.TokenManager {

    private static final int expiryDeltaOfSecond = 3 * 60;

    private static final String APP_ACCESS_TOKEN_KEY_PREFIX = "lark_app_access_token";

    private static final String TENANT_ACCESS_TOKEN_KEY_PREFIX = "lark_tenant_access_token";

    private final Cache cache;

    private final String cacheKeyPrefix;

    public TokenManager(Cache cache, String cacheKeyPrefix) {
        super(cache);

        this.cache = cache;
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    private String getAppAccessTokenKey(String appId) {
        String prefix = StringUtils.isNotEmpty(cacheKeyPrefix) ? (StringUtils.endsWithIgnoreCase(cacheKeyPrefix, ":") ? cacheKeyPrefix : (cacheKeyPrefix + ":")) : "";
        return prefix.concat(APP_ACCESS_TOKEN_KEY_PREFIX).concat("-").concat(appId);
    }

    @Override
    public String getAppAccessToken(Config config) throws Exception {
        // 缓存里存在则直接返回
        String token = cache.get(getAppAccessTokenKey(config.getAppId()));
        if (Strings.isNotEmpty(token)) {
            return token;
        }
        // 否则发起请求，获取token，然后缓存
        if (AppType.SELF_BUILT == config.getAppType()) {
            AppAccessTokenResp resp = getInternalAppAccessToken(config);
            token = resp.getAppAccessToken();
            int timeOut = resp.getExpire();
            // 缓存
            cache.set(getAppAccessTokenKey(config.getAppId()), token, timeOut - expiryDeltaOfSecond, TimeUnit.SECONDS);
            return token;
        } else {
            AppAccessTokenResp resp = getIsvAppAccessToken(config);
            token = resp.getAppAccessToken();
            int timeOut = resp.getExpire();
            // 缓存
            cache.set(getAppAccessTokenKey(config.getAppId()), token, timeOut - expiryDeltaOfSecond, TimeUnit.SECONDS);
            return token;
        }
    }

    // 自建应用获取 access token
    private AppAccessTokenResp getInternalAppAccessToken(Config config) throws Exception {
        SelfBuiltAppAccessTokenReq internalAccessTokenReq = new SelfBuiltAppAccessTokenReq();
        internalAccessTokenReq.setAppId(config.getAppId());
        internalAccessTokenReq.setAppSecret(config.getAppSecret());
        RawResponse resp = Transport.send(config
            , new RequestOptions(), "POST"
            , Constants.APP_ACCESS_TOKEN_INTERNAL_URL_PATH
            , Sets.newHashSet(AccessTokenType.None), internalAccessTokenReq);

        AppAccessTokenResp appAccessTokenResp = UnmarshalRespUtil.unmarshalResp(resp,
            AppAccessTokenResp.class);
        if (appAccessTokenResp.getCode() != 0) {
            throw new ObtainAccessTokenException("obtain internal app access token failure: "
                , String.format("code:%d,msg:%s", appAccessTokenResp.getCode(),
                appAccessTokenResp.getMsg()));
        }

        return appAccessTokenResp;
    }

    private AppAccessTokenResp getIsvAppAccessToken(Config config) throws Exception {
        // 缓存中是否存在appTicket
        String appTicket = GlobalAppTicketManager.getAppTicketManager().get(config);
        if (Strings.isEmpty(appTicket)) {
            throw new AppTicketIsEmptyException();
        }

        // 发起请求，获取token
        MarketplaceAppAccessTokenReq isvAppAccessTokenReq = new MarketplaceAppAccessTokenReq();
        isvAppAccessTokenReq.setAppId(config.getAppId());
        isvAppAccessTokenReq.setAppSecret(config.getAppSecret());
        isvAppAccessTokenReq.setAppTicket(appTicket);

        RawResponse resp = Transport.send(config
            , new RequestOptions(), "POST"
            , Constants.APP_ACCESS_TOKEN_ISV_URL_PATH
            , Sets.newHashSet(AccessTokenType.None), isvAppAccessTokenReq);

        // 结果处理
        AppAccessTokenResp appAccessTokenResp = UnmarshalRespUtil.unmarshalResp(resp,
            AppAccessTokenResp.class);
        if (appAccessTokenResp.getCode() != 0) {
            throw new ObtainAccessTokenException("obtain isv app access token failure: "
                , String.format("code:%d,msg:%s", appAccessTokenResp.getCode(),
                appAccessTokenResp.getMsg()));
        }
        return appAccessTokenResp;
    }

    private String getTenantAccessTokenKey(String appId, String tenantKey) {
        String prefix = StringUtils.isNotEmpty(cacheKeyPrefix) ? (StringUtils.endsWithIgnoreCase(cacheKeyPrefix, ":") ? cacheKeyPrefix : (cacheKeyPrefix + ":")) : "";
        String key = prefix.concat(TENANT_ACCESS_TOKEN_KEY_PREFIX).concat("-").concat(appId);
        if (StringUtils.isNotEmpty(tenantKey)) {
            key = key.concat("-").concat(tenantKey);
        }
        return key;
    }

    public String getTenantAccessToken(Config config, String tenantKey) throws Exception {
        // 缓存中存在，则直接返回
        String token = cache.get(getTenantAccessTokenKey(config.getAppId(), tenantKey));
        if (Strings.isNotEmpty(token)) {
            return token;
        }

        // 发起请求，获取token
        TenantAccessTokenResp resp;
        if (AppType.SELF_BUILT == config.getAppType()) {
            resp = getInternalTenantAccessToken(config);
            token = resp.getTenantAccessToken();
            int timeOut = resp.getExpire();
            cache.set(getTenantAccessTokenKey(config.getAppId(), tenantKey), token,
                timeOut - expiryDeltaOfSecond, TimeUnit.SECONDS);
        } else {
            resp = getIsvTenantAccessToken(config, tenantKey);
            token = resp.getTenantAccessToken();
            int timeOut = resp.getExpire();
            cache.set(getTenantAccessTokenKey(config.getAppId(), tenantKey), token,
                timeOut - expiryDeltaOfSecond, TimeUnit.SECONDS);
        }
        return token;
    }

    // get internal tenant access token
    private TenantAccessTokenResp getInternalTenantAccessToken(Config config) throws Exception {
        SelfBuiltAppAccessTokenReq internalAccessTokenReq = new SelfBuiltAppAccessTokenReq();
        internalAccessTokenReq.setAppId(config.getAppId());
        internalAccessTokenReq.setAppSecret(config.getAppSecret());

        RawResponse resp = Transport.send(config
            , new RequestOptions(), "POST"
            , Constants.TENANT_ACCESS_TOKEN_INTERNAL_URL_PATH
            , Sets.newHashSet(AccessTokenType.None), internalAccessTokenReq);

        TenantAccessTokenResp tenantAccessTokenResp = UnmarshalRespUtil.unmarshalResp(resp,
            TenantAccessTokenResp.class);

        if (tenantAccessTokenResp.getCode() != 0) {
            throw new ObtainAccessTokenException("obtain internal tenant access token failure: "
                , String.format("code:%d,msg:%s", tenantAccessTokenResp.getCode(),
                tenantAccessTokenResp.getMsg()));
        }
        return tenantAccessTokenResp;
    }

    // get internal tenant access token
    private TenantAccessTokenResp getIsvTenantAccessToken(Config config, String tenantKey)
        throws Exception {
        AppAccessTokenResp appAccessToken = getIsvAppAccessToken(config);

        MarketplaceTenantAccessTokenReq isvTenantAccessTokenReq = new MarketplaceTenantAccessTokenReq();
        isvTenantAccessTokenReq.setAppAccessToken(appAccessToken.getAppAccessToken());
        isvTenantAccessTokenReq.setTenantKey(tenantKey);

        RawResponse resp = Transport.send(config
            , new RequestOptions(), "POST"
            , Constants.TENANT_ACCESS_TOKEN_ISV_URL_PATH
            , Sets.newHashSet(AccessTokenType.None), isvTenantAccessTokenReq);

        TenantAccessTokenResp tenantAccessTokenResp = UnmarshalRespUtil.unmarshalResp(resp,
            TenantAccessTokenResp.class);

        if (tenantAccessTokenResp.getCode() != 0) {
            throw new ObtainAccessTokenException("obtain isv tenant access token failure: "
                , String.format("code:%d,msg:%s", tenantAccessTokenResp.getCode(),
                tenantAccessTokenResp.getMsg()));
        }
        return tenantAccessTokenResp;
    }

}
