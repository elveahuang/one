package cc.wdev.platform.commons.oapis.lark.token;

import cc.wdev.platform.commons.oapis.lark.cache.Cache;
import cc.wdev.platform.commons.oapis.lark.config.AppConfig;
import cc.wdev.platform.commons.oapis.lark.request.JsapiTicketReq;
import cc.wdev.platform.commons.oapis.lark.request.JsapiTicketResp;
import cc.wdev.platform.commons.utils.StringUtils;
import com.lark.oapi.core.Transport;
import com.lark.oapi.core.exception.ObtainAccessTokenException;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.lark.oapi.core.utils.Sets;
import com.lark.oapi.core.utils.UnmarshalRespUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author elvea
 */
public class TicketManager {

    private final static String JSAPI_TICKET_PATH = "/open-apis/jssdk/ticket/get";

    private static final int expiryDeltaOfSecond = 3 * 60;

    private static final String JSAPI_TICKET_PREFIX = "lark_jsapi_ticket";

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

    public String getJsapiTicket(AppConfig appConfig) throws Exception {
        // 缓存里存在则直接返回
        String ticket = cache.get(getJsapiTicketKey(appConfig.getAppId()));
        if (StringUtils.isNotEmpty(ticket)) {
            return ticket;
        }
        // 否则发起请求，获取token，然后缓存
        JsapiTicketResp resp = get(appConfig);
        ticket = resp.getData().getTicket();
        int timeOut = resp.getData().getExpire();
        // 缓存
        cache.set(getJsapiTicketKey(appConfig.getAppId()), ticket, timeOut - expiryDeltaOfSecond, TimeUnit.SECONDS);
        return ticket;
    }

    private JsapiTicketResp get(AppConfig appConfig) throws Exception {
        JsapiTicketReq jsapiTicketReq = new JsapiTicketReq();
        RawResponse resp = Transport.send(appConfig.getConfig(), new RequestOptions(), "POST", JSAPI_TICKET_PATH, Sets.newHashSet(AccessTokenType.Tenant), jsapiTicketReq);
        JsapiTicketResp jsapiTicketResp = UnmarshalRespUtil.unmarshalResp(resp, JsapiTicketResp.class);
        if (jsapiTicketResp.getCode() != 0) {
            throw new ObtainAccessTokenException("obtain internal app access token failure: ", String.format("code:%d,msg:%s", jsapiTicketResp.getCode(), jsapiTicketResp.getMsg()));
        }
        return jsapiTicketResp;
    }

}
