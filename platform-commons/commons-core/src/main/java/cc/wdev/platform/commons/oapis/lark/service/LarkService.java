package cc.wdev.platform.commons.oapis.lark.service;

import cc.wdev.platform.commons.oapis.lark.bean.JsapiSignature;
import cc.wdev.platform.commons.oapis.lark.config.AppConfig;
import cc.wdev.platform.commons.oapis.lark.message.LarkMessagePayload;
import cc.wdev.platform.commons.oapis.lark.message.LarkMessageResponse;
import com.lark.oapi.Client;
import com.lark.oapi.core.Config;
import com.lark.oapi.service.contact.ContactService;
import com.lark.oapi.service.im.ImService;

/**
 * 基于飞书官方SDK二次封装
 * <p>
 * <a href="https://github.com/larksuite/oapi-sdk-java">...</a>
 *
 * @author elvea
 */
public interface LarkService {

    /**
     * 创建默认租户配置
     *
     * @return {@see Config}
     */
    Client getClient();

    /**
     * 创建指定¬租户配置
     *
     * @return {@see Config}
     */
    Client getClient(AppConfig appConfig);

    /**
     * 创建默认租户配置
     *
     * @return {@see Config}
     */
    Config getConfig();

    /**
     * 创建指定¬租户配置
     *
     * @return {@see Config}
     */
    Config getConfig(AppConfig appConfig);

    /**
     * 企业自建应用 - 获取访问凭证
     */
    String getAppAccessToken() throws Exception;

    /**
     * 企业自建应用 - 获取访问凭证
     */
    String getAppAccessToken(AppConfig appConfig) throws Exception;

    /**
     * 企业自建应用 - 获取访问凭证
     */
    String getTenantAccessToken() throws Exception;

    /**
     * 企业自建应用 - 获取访问凭证
     */
    String getTenantAccessToken(AppConfig appConfig) throws Exception;

    /**
     * 企业自建应用 - 获取临时凭证
     */
    String getJsapiTicket() throws Exception;

    /**
     * 企业自建应用 - 获取临时凭证
     */
    String getJsapiTicket(AppConfig appConfig) throws Exception;

    /**
     * @see JsapiSignature
     */
    JsapiSignature createJsapiSignature(String url) throws Exception;

    /**
     * @see JsapiSignature
     */
    JsapiSignature createJsapiSignature(AppConfig appConfig, String url) throws Exception;

    /**
     * @return {@link ContactService}
     */
    ContactService getContactService();

    /**
     * @return {@link ContactService}
     */
    ContactService getContactService(AppConfig appConfig);

    /**
     * @return {@link ImService}
     */
    ImService getImService();

    /**
     * @return {@link ImService}
     */
    ImService getImService(AppConfig appConfig);

    LarkMessageResponse createMessage(LarkMessagePayload payload) throws Exception;

}
