package cc.wdev.platform.commons.oapis.weixin.storage;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.utils.StringUtils;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import me.chanjar.weixin.common.bean.WxAccessToken;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author elvea
 */
public class WxMaCacheConfigStorage extends WxMaDefaultConfigImpl {

    private static final String ACCESS_TOKEN_KEY = "wx_ma_access_token";

    private static final String ACCESS_TOKEN_LOCK_KEY = "wx_ma_access_token_lock";

    private static final String JSAPI_TICKET_KEY = "wx_ma_jsapi_ticket";

    private static final String JSAPI_TICKET_LOCK_KEY = "wx_ma_jsapi_ticket_lock";

    private static final String CARD_API_TICKET_KEY = "wx_ma_card_api_ticket";

    private static final String CARD_API_TICKET_LOCK_KEY = "wx_ma_card_api_ticket_lock";

    private final CacheService cacheService;

    private final String keyPrefix;

    private String accessTokenKey;

    private String accessTokenLockKey;

    private String jsapiTicketKey;

    private String jsapiTicketLockKey;

    private String cardApiTicketKey;

    private String cardApiTicketLockKey;

    public WxMaCacheConfigStorage(@NonNull CacheService cacheService, String keyPrefix) {
        this.cacheService = cacheService;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void setAppid(String appId) {
        super.setAppid(appId);

        String key = getAppid();
        String prefix = StringUtils.isNotEmpty(keyPrefix) ? (StringUtils.endsWithIgnoreCase(keyPrefix, ":") ? keyPrefix : (keyPrefix + ":")) : "";

        accessTokenKey = prefix.concat(ACCESS_TOKEN_KEY).concat("-").concat(key);
        accessTokenLockKey = prefix.concat(ACCESS_TOKEN_LOCK_KEY).concat("-").concat(key);
        jsapiTicketKey = prefix.concat(JSAPI_TICKET_KEY).concat("-").concat(key);
        jsapiTicketLockKey = prefix.concat(JSAPI_TICKET_LOCK_KEY).concat("-").concat(key);
        cardApiTicketKey = prefix.concat(CARD_API_TICKET_KEY).concat("-").concat(key);
        cardApiTicketLockKey = prefix.concat(CARD_API_TICKET_LOCK_KEY).concat("-").concat(key);
    }

    @Override
    public Lock getAccessTokenLock() {
        return this.cacheService.getLock(this.accessTokenLockKey);
    }

    @Override
    public Lock getCardApiTicketLock() {
        return this.cacheService.getLock(this.cardApiTicketLockKey);
    }

    @Override
    public Lock getJsapiTicketLock() {
        return this.cacheService.getLock(this.jsapiTicketLockKey);
    }

    @Override
    public String getAccessToken() {
        return cacheService.get(this.accessTokenKey);
    }

    @Override
    public boolean isAccessTokenExpired() {
        Long expire = cacheService.getExpire(this.accessTokenKey);
        return expire == null || expire < 2;
    }

    @Override
    public void updateAccessToken(WxAccessToken accessToken) {
        cacheService.set(this.accessTokenKey, accessToken.getAccessToken(), Duration.ofSeconds(accessToken.getExpiresIn()));
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresInSeconds) {
        cacheService.set(this.accessTokenKey, accessToken, Duration.ofSeconds(expiresInSeconds));
    }

    @Override
    public String getJsapiTicket() {
        return cacheService.get(this.jsapiTicketKey);
    }

    @Override
    public boolean isJsapiTicketExpired() {
        Long expire = cacheService.getExpire(this.jsapiTicketKey);
        return expire == null || expire < 2;
    }

    @Override
    public void expireJsapiTicket() {
        cacheService.expire(this.jsapiTicketKey, 0, TimeUnit.SECONDS);
    }

    @Override
    public void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        cacheService.set(this.jsapiTicketKey, jsapiTicket, Duration.ofSeconds(expiresInSeconds));
    }

    @Override
    public String getCardApiTicket() {
        return cacheService.get(cardApiTicketKey);
    }

    @Override
    public boolean isCardApiTicketExpired() {
        Long expire = cacheService.getExpire(this.cardApiTicketKey);
        return expire == null || expire < 2;
    }

    @Override
    public void expireCardApiTicket() {
        cacheService.expire(this.cardApiTicketKey, 0, TimeUnit.SECONDS);
    }

    @Override
    public void updateCardApiTicket(String cardApiTicket, int expiresInSeconds) {
        cacheService.set(this.cardApiTicketKey, cardApiTicket, Duration.ofSeconds(expiresInSeconds));
    }

    @Override
    public void expireAccessToken() {
        cacheService.expire(this.accessTokenKey, 0, TimeUnit.SECONDS);
    }

}
