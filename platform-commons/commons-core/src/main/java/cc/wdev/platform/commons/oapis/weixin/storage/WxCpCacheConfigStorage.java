package cc.wdev.platform.commons.oapis.weixin.storage;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.utils.StringUtils;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author elvea
 */
public class WxCpCacheConfigStorage extends WxCpDefaultConfigImpl {

    private static final String ACCESS_TOKEN_KEY = "wx_cp_access_token";
    private static final String ACCESS_TOKEN_LOCK_KEY = "wx_cp_access_token_lock";

    private static final String JSAPI_TICKET_KEY = "wx_cp_jsapi_ticket";
    private static final String JSAPI_TICKET_LOCK_KEY = "wx_cp_jsapi_ticket_lock";

    private static final String AGENT_JSAPI_TICKET_KEY = "wx_cp_agent_jsapi_ticket";
    private static final String AGENT_JSAPI_TICKET_LOCK_KEY = "wx_cp_agent_jsapi_ticket_lock";

    private final CacheService cacheService;

    private final String keyPrefix;

    private String accessTokenKey;

    private String accessTokenLockKey;

    private String jsapiTicketKey;

    private String jsapiTicketLockKey;

    private String agentJsapiTicketKey;

    private String agentJsapiTicketLockKey;

    public WxCpCacheConfigStorage(@NonNull CacheService cacheService, String keyPrefix) {
        this.cacheService = cacheService;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void setAgentId(Integer agentId) {
        super.setAgentId(agentId);

        String key = getCorpId().concat("-").concat(String.valueOf(agentId));
        String prefix = StringUtils.isNotEmpty(keyPrefix) ? (StringUtils.endsWithIgnoreCase(keyPrefix, ":") ? keyPrefix : (keyPrefix + ":")) : "";

        accessTokenKey = prefix.concat(ACCESS_TOKEN_KEY).concat("-").concat(key);
        accessTokenLockKey = prefix.concat(ACCESS_TOKEN_LOCK_KEY).concat("-").concat(key);
        jsapiTicketKey = prefix.concat(JSAPI_TICKET_KEY).concat("-").concat(key);
        jsapiTicketLockKey = prefix.concat(JSAPI_TICKET_LOCK_KEY).concat("-").concat(key);
        agentJsapiTicketKey = prefix.concat(AGENT_JSAPI_TICKET_KEY).concat("-").concat(key);
        agentJsapiTicketLockKey = prefix.concat(AGENT_JSAPI_TICKET_LOCK_KEY).concat("-").concat(key);
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
    public Lock getJsapiTicketLock() {
        return cacheService.getLock(this.jsapiTicketLockKey);
    }

    @Override
    public String getJsapiTicket() {
        return this.cacheService.get(this.jsapiTicketKey);
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
    public boolean isAgentJsapiTicketExpired() {
        Long expire = cacheService.getExpire(this.agentJsapiTicketKey);
        return expire == null || expire < 2;
    }

    @Override
    public Lock getAgentJsapiTicketLock() {
        return cacheService.getLock(this.agentJsapiTicketLockKey);
    }

    @Override
    public void expireAgentJsapiTicket() {
        cacheService.expire(this.agentJsapiTicketKey, 0, TimeUnit.SECONDS);
    }

    @Override
    public void updateAgentJsapiTicket(String agentJsapiTicket, int expiresInSeconds) {
        cacheService.set(this.agentJsapiTicketKey, agentJsapiTicket, Duration.ofSeconds(expiresInSeconds));
    }

    @Override
    public String getAgentJsapiTicket() {
        return cacheService.get(this.agentJsapiTicketKey);
    }

}
