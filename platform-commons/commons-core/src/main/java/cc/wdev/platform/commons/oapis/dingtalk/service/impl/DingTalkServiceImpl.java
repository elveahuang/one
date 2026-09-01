package cc.wdev.platform.commons.oapis.dingtalk.service.impl;

import cc.wdev.platform.commons.oapis.dingtalk.GlobalCacheManager;
import cc.wdev.platform.commons.oapis.dingtalk.GlobalTicketManager;
import cc.wdev.platform.commons.oapis.dingtalk.GlobalTokenManager;
import cc.wdev.platform.commons.oapis.dingtalk.bean.JsapiSignature;
import cc.wdev.platform.commons.oapis.dingtalk.cache.Cache;
import cc.wdev.platform.commons.oapis.dingtalk.config.AppConfig;
import cc.wdev.platform.commons.oapis.dingtalk.service.DingTalkService;
import cc.wdev.platform.commons.oapis.dingtalk.token.TicketManager;
import cc.wdev.platform.commons.oapis.dingtalk.token.TokenManager;
import cc.wdev.platform.commons.utils.EncryptUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetSsoUserInfoHeaders;
import com.aliyun.dingtalkoauth2_1_0.models.GetSsoUserInfoRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetSsoUserInfoResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 * @see DingTalkService
 */
@Slf4j
public class DingTalkServiceImpl implements DingTalkService {

    private final TokenManager tokenManager;

    private final TicketManager ticketManager;

    @Getter
    private final Cache cache;

    @Getter
    @Setter
    private AppConfig appConfig;

    public DingTalkServiceImpl(Cache cache, String cacheKeyPrefix) {
        this.cache = cache;
        this.tokenManager = new TokenManager(cache, cacheKeyPrefix);
        this.ticketManager = new TicketManager(cache, cacheKeyPrefix);
        //
        GlobalTicketManager.setTicketManager(this.ticketManager);
        GlobalTokenManager.setTokenManager(this.tokenManager);
        GlobalCacheManager.setCache(this.cache);
    }

    /**
     * @see DingTalkService#getAccessToken()
     */
    @Override
    public String getAccessToken() throws Exception {
        return this.getAccessToken(this.appConfig);
    }

    /**
     * @see DingTalkService#getAccessToken(AppConfig)
     */
    @Override
    public String getAccessToken(AppConfig config) throws Exception {
        return this.tokenManager.getAccessToken(config);
    }

    /**
     * @see DingTalkService#getJsapiTicket()
     */
    @Override
    public String getJsapiTicket() throws Exception {
        return this.getJsapiTicket(this.appConfig);
    }

    /**
     * @see DingTalkService#getJsapiTicket(AppConfig)
     */
    @Override
    public String getJsapiTicket(AppConfig config) throws Exception {
        return this.ticketManager.getJsapiTicket(config);
    }

    /**
     * @see DingTalkService#createJsapiSignature(String)
     */
    @Override
    public JsapiSignature createJsapiSignature(String url) throws Exception {
        return this.createJsapiSignature(this.appConfig, url);
    }

    /**
     * @see DingTalkService#createJsapiSignature(AppConfig, String)
     */
    @Override
    public JsapiSignature createJsapiSignature(AppConfig config, String url) throws Exception {
        long timestamp = System.currentTimeMillis() / 1000;
        String noncestr = StringUtils.randomAlphabetic(12);
        String jsapiTicket = this.getJsapiTicket(config);
        String signature = EncryptUtils.sha256(
            "jsapi_ticket=" + jsapiTicket,
            "noncestr=" + noncestr,
            "timestamp=" + timestamp,
            "url=" + url
        );
        JsapiSignature jsapiSignature = new JsapiSignature();
        jsapiSignature.setAgentId(config.getAgentId());
        jsapiSignature.setCorpId(config.getCorpId());
        jsapiSignature.setTimestamp(timestamp);
        jsapiSignature.setNonceStr(noncestr);
        jsapiSignature.setUrl(url);
        jsapiSignature.setSignature(signature);
        return jsapiSignature;
    }

    @Override
    public void getUserByCode(String code) throws Exception {
        this.getUserByCode(appConfig, code);
    }

    @Override
    public void getUserByCode(AppConfig config, String code) throws Exception {
        try {
            Client client = new Client(config.getConfig());
            GetSsoUserInfoHeaders headers = new GetSsoUserInfoHeaders();
            headers.xAcsDingtalkAccessToken = this.getAccessToken();
            GetSsoUserInfoRequest request = new GetSsoUserInfoRequest().setCode(code);
            GetSsoUserInfoResponse response = client.getSsoUserInfoWithOptions(request, headers, new RuntimeOptions());
            System.out.printf(response.getBody().userId);
        } catch (TeaException e) {
            if (!com.aliyun.teautil.Common.empty(e.code) && !com.aliyun.teautil.Common.empty(e.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("getUserByCode Error.", e);
            }
        }
    }

}
