package cc.wdev.platform.commons.oapis.lark.service.impl;

import cc.wdev.platform.commons.oapis.lark.GlobalCacheManager;
import cc.wdev.platform.commons.oapis.lark.GlobalTicketManager;
import cc.wdev.platform.commons.oapis.lark.GlobalTokenManager;
import cc.wdev.platform.commons.oapis.lark.bean.JsapiSignature;
import cc.wdev.platform.commons.oapis.lark.cache.Cache;
import cc.wdev.platform.commons.oapis.lark.config.AppConfig;
import cc.wdev.platform.commons.oapis.lark.message.LarkMessagePayload;
import cc.wdev.platform.commons.oapis.lark.message.LarkMessageResponse;
import cc.wdev.platform.commons.oapis.lark.service.LarkService;
import cc.wdev.platform.commons.oapis.lark.token.TicketManager;
import cc.wdev.platform.commons.oapis.lark.token.TokenManager;
import cc.wdev.platform.commons.utils.EncryptUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.lark.oapi.Client;
import com.lark.oapi.core.Config;
import com.lark.oapi.service.contact.ContactService;
import com.lark.oapi.service.im.ImService;
import com.lark.oapi.service.im.v1.enums.CreateMessageCreateMessageV1ReceiveIDTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import lombok.Setter;

/**
 * @author elvea
 */
public class LarkServiceImpl implements LarkService {

    private final TokenManager tokenManager;

    private final TicketManager ticketManager;

    private final Cache cache;

    @Setter
    private AppConfig appConfig;

    public LarkServiceImpl(Cache cache, String cacheKeyPrefix) {
        this.cache = cache;
        this.tokenManager = new TokenManager(cache, cacheKeyPrefix);
        this.ticketManager = new TicketManager(cache, cacheKeyPrefix);
        //
        GlobalTicketManager.setTicketManager(this.ticketManager);
        GlobalTokenManager.setTokenManager(this.tokenManager);
        GlobalCacheManager.setCache(this.cache);
    }

    /**
     * @see LarkService#getClient()
     */
    @Override
    public Client getClient() {
        return this.getClient(appConfig);
    }

    /**
     * @see LarkService#getClient(AppConfig)
     */
    @Override
    public Client getClient(AppConfig appConfig) {
        return Client.newBuilder(appConfig.getAppId(), appConfig.getAppSecret())
            .appType(appConfig.getAppType())
            .tokenCache(cache)
            .build();
    }

    /**
     * @see LarkService#getAppAccessToken()
     */
    @Override
    public Config getConfig() {
        return this.getConfig(appConfig);
    }

    /**
     * @see LarkService#getAppAccessToken()
     */
    @Override
    public Config getConfig(AppConfig appConfig) {
        Config config = new Config();
        config.setAppType(appConfig.getAppType());
        config.setAppId(appConfig.getAppId());
        config.setAppSecret(appConfig.getAppSecret());
        config.setCache(this.cache);
        return config;
    }

    /**
     * @see LarkService#getAppAccessToken()
     */
    @Override
    public String getAppAccessToken() throws Exception {
        return this.getAppAccessToken(this.appConfig);
    }

    /**
     * @see LarkService#getAppAccessToken(AppConfig)
     */
    @Override
    public String getAppAccessToken(AppConfig appConfig) throws Exception {
        return this.tokenManager.getAppAccessToken(this.appConfig.getConfig());
    }

    /**
     * @see LarkService#getTenantAccessToken()
     */
    @Override
    public String getTenantAccessToken() throws Exception {
        return this.getTenantAccessToken(this.appConfig);
    }

    /**
     * @see LarkService#getTenantAccessToken(AppConfig)
     */
    @Override
    public String getTenantAccessToken(AppConfig config) throws Exception {
        return this.tokenManager.getTenantAccessToken(this.appConfig.getConfig(), null);
    }

    /**
     * @see LarkService#getJsapiTicket()
     */
    @Override
    public String getJsapiTicket() throws Exception {
        return this.getJsapiTicket(this.appConfig);
    }

    /**
     * @see LarkService#getJsapiTicket(AppConfig)
     */
    @Override
    public String getJsapiTicket(AppConfig config) throws Exception {
        return this.ticketManager.getJsapiTicket(config);
    }

    /**
     * @see LarkService#createJsapiSignature(String)
     */
    @Override
    public JsapiSignature createJsapiSignature(String url) throws Exception {
        return this.createJsapiSignature(this.appConfig, url);
    }

    /**
     * @see LarkService#createJsapiSignature(AppConfig, String)
     */
    @Override
    public JsapiSignature createJsapiSignature(AppConfig config, String url) throws Exception {
        long timestamp = System.currentTimeMillis();
        String noncestr = StringUtils.randomAlphabetic(12);
        String jsapiTicket = getJsapiTicket(config);
        String signature = EncryptUtils.sha(
            "jsapi_ticket=" + jsapiTicket,
            "noncestr=" + noncestr,
            "timestamp=" + timestamp,
            "url=" + url
        );
        JsapiSignature jsapiSignature = new JsapiSignature();
        jsapiSignature.setAppId(config.getAppId());
        jsapiSignature.setTimestamp(timestamp);
        jsapiSignature.setNonceStr(noncestr);
        jsapiSignature.setUrl(url);
        jsapiSignature.setSignature(signature);
        return jsapiSignature;
    }

    /**
     * @see LarkService#getContactService()
     */
    @Override
    public ContactService getContactService() {
        return this.getContactService(this.appConfig);
    }

    /**
     * @see LarkService#getContactService(AppConfig)
     */
    @Override
    public ContactService getContactService(AppConfig appConfig) {
        return new ContactService(this.appConfig.getConfig());
    }

    /**
     * @see LarkService#getImService()
     */
    @Override
    public ImService getImService() {
        return this.getImService(this.appConfig);
    }

    /**
     * @see LarkService#getImService(AppConfig)
     */
    @Override
    public ImService getImService(AppConfig appConfig) {
        return new ImService(this.appConfig.getConfig());
    }

    /**
     * @see LarkService#createMessage(LarkMessagePayload)
     */
    @Override
    public LarkMessageResponse createMessage(LarkMessagePayload payload) throws Exception {
        CreateMessageReqBody messageReqBody = CreateMessageReqBody.newBuilder()
            .content(GsonUtils.toJson(payload.getJson()))
            .msgType(payload.getType())
            .receiveId(payload.getReceiveId())
            .build();
        CreateMessageReq messageReq = CreateMessageReq.newBuilder()
            .createMessageReqBody(messageReqBody)
            .receiveIdType(CreateMessageCreateMessageV1ReceiveIDTypeEnum.USER_ID)
            .build();
        CreateMessageResp resp = getImService().message().create(messageReq);
        return LarkMessageResponse.builder().code(resp.getCode()).build();
    }

}
