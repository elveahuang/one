package cc.wdev.platform.commons.oapis.weixin.storage;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.utils.StringUtils;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.enums.TicketType;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author elvea
 */
public class WxMpCacheConfigStorage extends WxMpDefaultConfigImpl {

    private static final String ACCESS_TOKEN_KEY = "wx_mp_access_token";

    private static final String ACCESS_TOKEN_LOCK_KEY = "wx_mp_access_token_lock";

    private static final String JSAPI_TICKET_KEY = "wx_mp_jsapi_ticket";

    private static final String JSAPI_TICKET_LOCK_KEY = "wx_mp_jsapi_ticket_lock";

    private static final String SDK_TICKET_KEY = "wx_mp_sdk_ticket";

    private static final String SDK_TICKET_LOCK_KEY = "wx_mp_sdk_ticket_lock";

    private static final String CARD_API_TICKET_KEY = "wx_mp_card_api_ticket";

    private static final String CARD_API_TICKET_LOCK_KEY = "wx_mp_card_api_ticket_lock";

    private final CacheService cacheService;

    private final String keyPrefix;

    private String accessTokenKey;

    private String accessTokenLockKey;

    private String jsapiTicketKey;

    private String jsapiTicketLockKey;

    private String sdkTicketKey;

    private String sdkTicketLockKey;

    private String cardApiTicketKey;

    private String cardApiTicketLockKey;

    public WxMpCacheConfigStorage(@NonNull CacheService cacheService, String keyPrefix) {
        this.cacheService = cacheService;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void setAppId(String appId) {
        super.setAppId(appId);

        String key = getAppId();
        String prefix = StringUtils.isNotEmpty(keyPrefix) ? (StringUtils.endsWithIgnoreCase(keyPrefix, ":") ? keyPrefix : (keyPrefix + ":")) : "";

        accessTokenKey = prefix.concat(ACCESS_TOKEN_KEY).concat("-").concat(key);
        accessTokenLockKey = prefix.concat(ACCESS_TOKEN_LOCK_KEY).concat("-").concat(key);
        jsapiTicketKey = prefix.concat(JSAPI_TICKET_KEY).concat("-").concat(key);
        jsapiTicketLockKey = prefix.concat(JSAPI_TICKET_LOCK_KEY).concat("-").concat(key);
        sdkTicketKey = prefix.concat(SDK_TICKET_KEY).concat("-").concat(key);
        sdkTicketLockKey = prefix.concat(SDK_TICKET_LOCK_KEY).concat("-").concat(key);
        cardApiTicketKey = prefix.concat(CARD_API_TICKET_KEY).concat("-").concat(key);
        cardApiTicketLockKey = prefix.concat(CARD_API_TICKET_LOCK_KEY).concat("-").concat(key);
    }

    @Override
    public Lock getAccessTokenLock() {
        return cacheService.getLock(this.accessTokenLockKey);
    }

    @Override
    public String getAccessToken() {
        return this.cacheService.get(this.accessTokenKey);
    }

    @Override
    public boolean isAccessTokenExpired() {
        Long expire = cacheService.getExpire(this.accessTokenKey);
        return expire == null || expire < 2;
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresInSeconds) {
        cacheService.set(this.accessTokenKey, accessToken, Duration.ofSeconds(expiresInSeconds));
    }

    @Override
    public void updateAccessToken(WxAccessToken accessToken) {
        cacheService.set(this.accessTokenKey, accessToken.getAccessToken(), Duration.ofSeconds(accessToken.getExpiresIn()));
    }

    @Override
    public void expireAccessToken() {
        cacheService.expire(this.accessTokenKey, 0, TimeUnit.SECONDS);
    }

    @Override
    public String getTicket(TicketType type) {
        return cacheService.get(this.getTicketRedisKey(type));
    }

    @Override
    public Lock getTicketLock(TicketType type) {
        return this.cacheService.getLock(this.getTicketRedisLockKey(type));
    }

    @Override
    public boolean isTicketExpired(TicketType type) {
        return cacheService.getExpire(this.getTicketRedisKey(type)) < 2;
    }

    @Override
    public synchronized void updateTicket(TicketType type, String jsapiTicket, int expiresInSeconds) {
        cacheService.set(this.getTicketRedisKey(type), jsapiTicket, Duration.ofSeconds(expiresInSeconds - 200));
    }

    @Override
    public void expireTicket(TicketType type) {
        cacheService.expire(this.getTicketRedisKey(type), 0, TimeUnit.SECONDS);
    }

    protected String getTicketRedisKey(TicketType type) {
        if (TicketType.SDK.equals(type)) {
            return this.sdkTicketKey;
        } else if (TicketType.WX_CARD.equals(type)) {
            return this.cardApiTicketKey;
        } else if (TicketType.JSAPI.equals(type)) {
            return this.jsapiTicketKey;
        }
        return this.jsapiTicketKey;
    }

    protected String getTicketRedisLockKey(TicketType type) {
        if (TicketType.SDK.equals(type)) {
            return this.sdkTicketLockKey;
        } else if (TicketType.WX_CARD.equals(type)) {
            return this.cardApiTicketLockKey;
        } else if (TicketType.JSAPI.equals(type)) {
            return this.jsapiTicketLockKey;
        }
        return this.jsapiTicketLockKey;
    }

}
