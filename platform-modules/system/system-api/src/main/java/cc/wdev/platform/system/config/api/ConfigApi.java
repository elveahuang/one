package cc.wdev.platform.system.config.api;

import cc.wdev.platform.commons.core.mail.MailConfig;
import cc.wdev.platform.commons.oapis.location.LocationConfig;
import cc.wdev.platform.commons.oapis.sms.SmsConfig;
import cc.wdev.platform.system.config.domain.form.AppBaseSettingForm;
import cc.wdev.platform.system.config.domain.form.AppPageForm;
import cc.wdev.platform.system.config.domain.request.ConfigSaveRequest;
import cc.wdev.platform.system.config.domain.vo.ConfigVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/config")
public interface ConfigApi {

    /**
     * 初始化系统配置项
     */
    void initialize();

    /**
     * 获取配置项
     */
    @GetExchange
    ConfigVo getConfig(@Parameter(description = "配置键") @RequestParam(value = "key") String key);

    /**
     * 保存配置项
     */
    void saveConfig(ConfigSaveRequest request);

    /**
     * 获取配置项
     */
    @GetExchange("/get-as-string")
    String getString(@Parameter(description = "配置键") @RequestParam(value = "key") String key);

    /**
     * 获取配置项
     */
    @GetExchange("/get-as-string-with-default-value")
    String getString(@Parameter(description = "配置键") @RequestParam(value = "key") String key,
                     @Parameter(description = "默认值") @RequestParam(value = "defaultValue") String defaultValue);

    /**
     * 获取配置项
     */
    @GetExchange("/get-as-boolean")
    boolean getBoolean(@Parameter(description = "配置键") @RequestParam(value = "key") String key);

    /**
     * 获取配置项
     */
    @GetExchange("/get-as-boolean-with-default-value")
    boolean getBoolean(@Parameter(description = "配置键") @RequestParam(value = "key") String key,
                       @Parameter(description = "默认值") @RequestParam(value = "defaultValue") boolean defaultValue);

    long getLong(String key);

    long getLong(String key, long defaultValue);

    int getInt(String key);

    int getInt(String key, int defaultValue);

    MailConfig getMailConfig();

    void saveMailConfig(MailConfig config);

    SmsConfig getSmsConfig();

    void saveSmsConfig(SmsConfig config);

    AppBaseSettingForm getAppBaseInfo();

    void saveAppBaseInfo(AppBaseSettingForm form);

    List<AppPageForm> getAgreements();

    void saveAgreement(AppPageForm form);

    List<AppPageForm> getPages();

    void savePage(AppPageForm form);

    LocationConfig getLocationConfig();

    void saveLocationConfig(LocationConfig config);

}
