package cc.wdev.platform.system.config.api;

import cc.wdev.platform.commons.core.mail.MailConfig;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.oapis.location.LocationConfig;
import cc.wdev.platform.commons.oapis.location.enums.LocationTypeEnum;
import cc.wdev.platform.commons.oapis.location.tianditu.TiandituConfig;
import cc.wdev.platform.commons.oapis.sms.SmsConfig;
import cc.wdev.platform.commons.oapis.sms.aliyun.AliyunSmsSender;
import cc.wdev.platform.commons.oapis.sms.enums.SmsTypeEnum;
import cc.wdev.platform.commons.oapis.sms.tencent.TencentSmsSender;
import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.config.domain.entity.ConfigEntity;
import cc.wdev.platform.system.config.domain.form.AppBaseSettingForm;
import cc.wdev.platform.system.config.domain.form.AppPageForm;
import cc.wdev.platform.system.config.domain.request.ConfigGetRequest;
import cc.wdev.platform.system.config.domain.request.ConfigSaveRequest;
import cc.wdev.platform.system.config.domain.vo.ConfigVo;
import cc.wdev.platform.system.config.enums.BaseConfigBizTypeEnum;
import cc.wdev.platform.system.config.enums.ConfigBizTypeEnum;
import cc.wdev.platform.system.config.enums.ConfigContentTypeEnum;
import cc.wdev.platform.system.config.enums.ConfigGroupTypeEnum;
import cc.wdev.platform.system.config.service.ConfigService;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.service.TenantService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigApiImpl implements ConfigApi {

    private final ConfigService configService;

    private final TenantService tenantService;

    /**
     * @see ConfigApi#initialize()
     */
    @Override
    public void initialize() {
        List<BaseConfigBizTypeEnum> configBizTypeEnums = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseConfigBizTypeEnum.class);

        List<BaseConfigBizTypeEnum> platformConfigEnums = Lists.newArrayList();
        List<BaseConfigBizTypeEnum> systemConfigEnums = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(configBizTypeEnums)) {
            // 平台范围配置项
            platformConfigEnums.addAll(configBizTypeEnums.stream().filter((e) -> e.getScope().equals(BizScopeTypeEnum.PLATFORM.getCode())).toList());
            // 系统范围配置项
            systemConfigEnums.addAll(configBizTypeEnums.stream().filter((e) -> e.getScope().equals(BizScopeTypeEnum.SYSTEM.getCode())).toList());
        }

        // 待处理配置项实体
        List<ConfigEntity> updateList = Lists.newArrayList();
        List<ConfigEntity> createList = Lists.newArrayList();

        // 获取租户列表
        List<TenantEntity> tenants = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenants)) {
            return;
        }

        for (TenantEntity tenant : tenants) {
            List<BaseConfigBizTypeEnum> tenantConfigEnumList = Lists.newArrayList();
            if (tenant.getRootInd() == BooleanTypeEnum.getTrueValue()) {
                tenantConfigEnumList.addAll(platformConfigEnums);
            }
            tenantConfigEnumList.addAll(systemConfigEnums);

            if (CollectionUtils.isEmpty(tenantConfigEnumList)) {
                continue;
            }
            for (BaseConfigBizTypeEnum configBizTypeEnum : tenantConfigEnumList) {
                Long tid = 0L;
                if (!configBizTypeEnum.getScope().equalsIgnoreCase(BizScopeTypeEnum.PLATFORM.getCode())) {
                    tid = tenant.getId();
                }

                ConfigEntity entity = this.configService.getConfigEntity(ConfigGetRequest.builder()
                    .tenantId(tid)
                    .configKey(configBizTypeEnum.getCode())
                    .build());
                if (entity != null) {
                    updateList.add(entity);
                } else {
                    entity = new ConfigEntity();
                    entity.setTenantId(tid);
                    entity.setConfigContentType(configBizTypeEnum.getContentType());
                    entity.setConfigGroupType(configBizTypeEnum.getGroupType());
                    entity.setConfigKey(configBizTypeEnum.getValue());
                    entity.setDescription(configBizTypeEnum.getDescription());
                    entity.setTitle(configBizTypeEnum.getDescription());
                    entity.setLabel(configBizTypeEnum.getLabelKey());
                    createList.add(entity);
                }
            }
        }
        this.configService.insertBatch(createList);
        this.configService.updateBatchById(updateList);
    }

    /**
     * @see ConfigApi#getConfig(String)
     */
    @Override
    public ConfigVo getConfig(@RequestParam(value = "key") String key) {
        return this.configService.getConfig(key);
    }

    /**
     * @see ConfigApi#saveConfig(ConfigSaveRequest)
     */
    @Override
    public void saveConfig(ConfigSaveRequest request) {
        this.configService.saveConfig(request);
    }

    /**
     * @see ConfigApi#getString(String)
     */
    @Override
    public String getString(@RequestParam(value = "key") String key) {
        return this.getString(key, "");
    }

    /**
     * @see ConfigApi#getString(String, String)
     */
    @Override
    public String getString(@RequestParam(value = "key") String key,
                            @RequestParam(value = "defaultValue") String defaultValue) {
        ConfigVo config = this.getConfig(key);
        if (config != null) {
            return StringUtils.isNotEmpty(config.getConfigValue()) ? config.getConfigValue() : defaultValue;
        }
        return defaultValue;
    }

    /**
     * @see ConfigApi#getBoolean(String)
     */
    @Override
    public boolean getBoolean(@RequestParam(value = "key") String key) {
        return this.getBoolean(key, false);
    }

    /**
     * @see ConfigApi#getBoolean(String, boolean)
     */
    @Override
    public boolean getBoolean(@RequestParam(value = "key") String key,
                              @RequestParam(value = "defaultValue") boolean defaultValue) {
        ConfigVo config = this.getConfig(key);
        if (config != null) {
            return "true".equalsIgnoreCase(config.getConfigValue()) || "1".equalsIgnoreCase(config.getConfigValue());
        }
        return defaultValue;
    }

    /**
     * @see ConfigApi#getLong(String)
     */
    @Override
    public long getLong(String key) {
        return this.getLong(key, 0L);
    }

    /**
     * @see ConfigApi#getBoolean(String)
     */
    @Override
    public long getLong(String key, long defaultValue) {
        ConfigVo config = this.getConfig(key);
        if (config != null) {
            try {
                return Long.parseLong(config.getConfigValue());
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return defaultValue;
    }

    /**
     * @see ConfigApi#getInt(String)
     */
    @Override
    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    /**
     * @see ConfigApi#getInt(String)
     */
    @Override
    public int getInt(String key, int defaultValue) {
        ConfigVo config = this.getConfig(key);
        if (config != null) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return defaultValue;
    }

    /**
     * @see ConfigApi#getMailConfig()
     */
    @Override
    public MailConfig getMailConfig() {
        return MailConfig.builder()
            .enabled(this.getBoolean(ConfigBizTypeEnum.MAIL_SERVER_ENABLED.getValue(), false))
            .auth(this.getBoolean(ConfigBizTypeEnum.MAIL_SERVER_AUTH.getValue(), true))
            .ssl(this.getBoolean(ConfigBizTypeEnum.MAIL_SERVER_SSL.getValue(), true))
            .host(this.getString(ConfigBizTypeEnum.MAIL_SERVER_HOST.getValue(), "127.0.0.1"))
            .port(this.getInt(ConfigBizTypeEnum.MAIL_SERVER_PORT.getValue(), 465))
            .username(this.getString(ConfigBizTypeEnum.MAIL_SERVER_USER.getValue()))
            .password(this.getString(ConfigBizTypeEnum.MAIL_SERVER_PASS.getValue()))
            .from(this.getString(ConfigBizTypeEnum.MAIL_SERVER_FROM.getValue()))
            .name(this.getString(ConfigBizTypeEnum.MAIL_SERVER_NAME.getValue()))
            .build();
    }

    /**
     * @see ConfigApi#saveMailConfig(MailConfig)
     */
    @Override
    public void saveMailConfig(MailConfig config) {
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_ENABLED.getValue())
            .configValue(String.valueOf(config.isEnabled()))
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.BOOL.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_AUTH.getValue())
            .configValue(String.valueOf(config.isAuth()))
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.BOOL.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_SSL.getValue())
            .configValue(String.valueOf(config.isSsl()))
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.BOOL.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_PROTOCOL.getValue())
            .configValue(String.valueOf(config.getSslProtocol()))
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.BOOL.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_FROM.getValue())
            .configValue(config.getFrom())
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_HOST.getValue())
            .configValue(config.getHost())
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_PORT.getValue())
            .configValue(String.valueOf(config.getPort()))
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_NAME.getValue())
            .configValue(String.valueOf(config.getName()))
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_USER.getValue())
            .configValue(config.getUsername())
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.MAIL_SERVER_PASS.getValue())
            .configValue(config.getPassword())
            .configGroupType(ConfigGroupTypeEnum.MAIL.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
    }

    /**
     * @see ConfigApi#getSmsConfig()
     */
    @Override
    public SmsConfig getSmsConfig() {
        String type = this.getString(ConfigBizTypeEnum.SMS_PROVIDER.getValue());
        return SmsConfig.builder()
            .type(BaseEnum.getEnumByValue(type, SmsTypeEnum.class, SmsTypeEnum.None))
            .aliyun(AliyunSmsSender.Config.builder()
                .endpoint(this.getString(ConfigBizTypeEnum.SMS_ALIYUN_ENDPOINT.getValue()))
                .accessKeyId(this.getString(ConfigBizTypeEnum.SMS_ALIYUN_ACCESS_KEY_ID.getValue()))
                .accessKeySecret(this.getString(ConfigBizTypeEnum.SMS_ALIYUN_ACCESS_KEY_SECRET.getValue()))
                .signName(this.getString(ConfigBizTypeEnum.SMS_ALIYUN_SIGN_NAME.getValue()))
                .build())
            .tencent(TencentSmsSender.Config.builder()
                .endpoint(this.getString(ConfigBizTypeEnum.SMS_TENCENT_ENDPOINT.getValue()))
                .region(this.getString(ConfigBizTypeEnum.SMS_TENCENT_REGION.getValue()))
                .appId(this.getString(ConfigBizTypeEnum.SMS_TENCENT_APP_ID.getValue()))
                .secretId(this.getString(ConfigBizTypeEnum.SMS_TENCENT_SECRET_ID.getValue()))
                .secretKey(this.getString(ConfigBizTypeEnum.SMS_TENCENT_SECRET_KEY.getValue()))
                .signName(this.getString(ConfigBizTypeEnum.SMS_TENCENT_SIGN_NAME.getValue()))
                .build())
            .build();
    }

    /**
     * @see ConfigApi#saveSmsConfig(SmsConfig)
     */
    @Override
    public void saveSmsConfig(SmsConfig config) {
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_PROVIDER.getValue())
            .configValue(config.getType().name())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        //保存阿里云短信相关配置
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_ALIYUN_ENDPOINT.getValue())
            .configValue(config.getAliyun().getEndpoint())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_ALIYUN_ACCESS_KEY_ID.getValue())
            .configValue(config.getAliyun().getAccessKeyId())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_ALIYUN_ACCESS_KEY_SECRET.getValue())
            .configValue(config.getAliyun().getAccessKeySecret())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_ALIYUN_SIGN_NAME.getValue())
            .configValue(config.getAliyun().getSignName())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        //保存腾讯云短信相关配置
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_TENCENT_ENDPOINT.getValue())
            .configValue(config.getTencent().getEndpoint())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_TENCENT_REGION.getValue())
            .configValue(config.getTencent().getRegion())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_TENCENT_APP_ID.getValue())
            .configValue(config.getTencent().getAppId())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_TENCENT_SECRET_ID.getValue())
            .configValue(config.getTencent().getSecretId())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_TENCENT_SECRET_KEY.getValue())
            .configValue(config.getTencent().getSecretKey())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.SMS_TENCENT_SIGN_NAME.getValue())
            .configValue(config.getTencent().getSignName())
            .configGroupType(ConfigGroupTypeEnum.SMS.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
    }

    /**
     * @see ConfigApi#getAppBaseInfo()
     */
    @Override
    public AppBaseSettingForm getAppBaseInfo() {
        return AppBaseSettingForm.builder()
            .title(this.getString(ConfigBizTypeEnum.APP_TITLE.getValue()))
            .logo(this.getString(ConfigBizTypeEnum.APP_LOGO.getValue()))
            .copyright(this.getString(ConfigBizTypeEnum.APP_COPYRIGHT.getValue()))
            .build();
    }

    /**
     * @see ConfigApi#saveAppBaseInfo(AppBaseSettingForm)
     */
    @Override
    public void saveAppBaseInfo(AppBaseSettingForm form) {
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.APP_TITLE.getValue())
            .configValue(form.getTitle())
            .configGroupType(ConfigGroupTypeEnum.BASE.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.APP_COPYRIGHT.getValue())
            .configValue(form.getCopyright())
            .configGroupType(ConfigGroupTypeEnum.BASE.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.APP_LOGO.getValue())
            .configValue(form.getLogo())
            .configGroupType(ConfigGroupTypeEnum.BASE.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
    }

    /**
     * @see ConfigApi#getAgreements()
     */
    @Override
    public List<AppPageForm> getAgreements() {
        List<AppPageForm> items = new ArrayList<>(3);
        items.add(AppPageForm.builder()
            .code(ConfigBizTypeEnum.APP_AGREEMENT_MEMBER.getValue())
            .content(this.getString(ConfigBizTypeEnum.APP_AGREEMENT_MEMBER.getValue()))
            .build());
        items.add(AppPageForm.builder()
            .code(ConfigBizTypeEnum.APP_AGREEMENT_USER.getValue())
            .content(this.getString(ConfigBizTypeEnum.APP_AGREEMENT_USER.getValue()))
            .build());
        items.add(AppPageForm.builder()
            .code(ConfigBizTypeEnum.APP_AGREEMENT_PRIVACY_POLICY.getValue())
            .content(this.getString(ConfigBizTypeEnum.APP_AGREEMENT_PRIVACY_POLICY.getValue()))
            .build());
        return items;
    }

    /**
     * @see ConfigApi#saveAgreement(AppPageForm)
     */
    @Override
    public void saveAgreement(AppPageForm form) {
        ConfigBizTypeEnum configBizTypeEnum = BaseEnum.getEnumByValue(form.getCode(), ConfigBizTypeEnum.class);
        if (configBizTypeEnum != null && (
            configBizTypeEnum.equals(ConfigBizTypeEnum.APP_AGREEMENT_MEMBER) ||
                configBizTypeEnum.equals(ConfigBizTypeEnum.APP_AGREEMENT_USER) ||
                configBizTypeEnum.equals(ConfigBizTypeEnum.APP_AGREEMENT_PRIVACY_POLICY)
        )) {
            this.saveConfig(ConfigSaveRequest.builder()
                .configKey(configBizTypeEnum.getCode())
                .configValue(form.getContent())
                .build());
        }
    }

    /**
     * @see ConfigApi#getPages()
     */
    @Override
    public List<AppPageForm> getPages() {
        List<AppPageForm> items = new ArrayList<>(2);
        items.add(AppPageForm.builder()
            .code(ConfigBizTypeEnum.APP_ABOUT.getValue())
            .content(this.getString(ConfigBizTypeEnum.APP_ABOUT.getValue()))
            .build());
        items.add(AppPageForm.builder()
            .code(ConfigBizTypeEnum.APP_CONTACT.getValue())
            .content(this.getString(ConfigBizTypeEnum.APP_CONTACT.getValue()))
            .build());
        return items;
    }

    /**
     * @see ConfigApi#savePage(AppPageForm)
     */
    @Override
    public void savePage(AppPageForm form) {
        ConfigBizTypeEnum configBizTypeEnum = BaseEnum.getEnumByValue(form.getCode(), ConfigBizTypeEnum.class);
        if (configBizTypeEnum != null && (
            configBizTypeEnum.equals(ConfigBizTypeEnum.APP_ABOUT) || configBizTypeEnum.equals(ConfigBizTypeEnum.APP_CONTACT)
        )) {
            this.saveConfig(ConfigSaveRequest.builder()
                .configKey(configBizTypeEnum.getCode())
                .configValue(form.getContent())
                .build());
        }
    }

    @Override
    public LocationConfig getLocationConfig() {
        String type = this.getString(ConfigBizTypeEnum.LOCATION_PROVIDER.getValue());
        return LocationConfig.builder()
            .type(BaseEnum.getEnumByValue(type, LocationTypeEnum.class, LocationTypeEnum.None))
            .tianditu(TiandituConfig.builder()
                .token(this.getString(ConfigBizTypeEnum.LOCATION_TIANDITU_TOKEN.getValue()))
                .build())
            .build();
    }

    @Override
    public void saveLocationConfig(LocationConfig config) {
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.LOCATION_PROVIDER.getValue())
            .configValue(config.getType().name())
            .configGroupType(ConfigGroupTypeEnum.LOCATION.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
        configService.saveConfig(ConfigSaveRequest.builder()
            .configKey(ConfigBizTypeEnum.LOCATION_TIANDITU_TOKEN.getValue())
            .configValue(config.getTianditu().getToken())
            .configGroupType(ConfigGroupTypeEnum.LOCATION.getValue())
            .configContentType(ConfigContentTypeEnum.TEXT.getCode())
            .build());
    }
}
