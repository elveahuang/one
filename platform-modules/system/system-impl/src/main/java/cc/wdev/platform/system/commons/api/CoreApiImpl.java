package cc.wdev.platform.system.commons.api;

import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.extensions.ip.GlobalIpManager;
import cc.wdev.platform.commons.extensions.ip.Ip;
import cc.wdev.platform.commons.oapis.location.LocationConfig;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.utils.WebServletUtils;
import cc.wdev.platform.system.ai.api.AiAgentApi;
import cc.wdev.platform.system.ai.api.AiKbApi;
import cc.wdev.platform.system.ai.api.AiModelApi;
import cc.wdev.platform.system.ai.api.AiToolApi;
import cc.wdev.platform.system.commons.domain.vo.AppVo;
import cc.wdev.platform.system.commons.domain.vo.InitializeVo;
import cc.wdev.platform.system.commons.domain.vo.PageVo;
import cc.wdev.platform.system.config.api.BizTypeApi;
import cc.wdev.platform.system.config.api.ConfigApi;
import cc.wdev.platform.system.config.enums.ConfigBizTypeEnum;
import cc.wdev.platform.system.dict.api.DictApi;
import cc.wdev.platform.system.job.api.JobApi;
import cc.wdev.platform.system.message.api.MessageApi;
import cc.wdev.platform.system.open.api.WxMpApi;
import cc.wdev.platform.system.region.api.RegionApi;
import cc.wdev.platform.system.security.api.SecurityApi;
import cc.wdev.platform.system.tag.api.TagApi;
import com.google.common.collect.Lists;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class CoreApiImpl implements CoreApi {

    private final ConfigApi configApi;

    private final BizTypeApi bizTypeApi;

    private final SecurityApi securityApi;

    private final MessageApi messageApi;

    private final AiAgentApi aiAgentApi;

    private final AiKbApi aiKbApi;

    private final AiToolApi aiToolApi;

    private final AiModelApi aiModelApi;

    private final RegionApi regionApi;

    private final WxMpApi wxMpApi;

    private final DictApi dictApi;

    private final TagApi tagApi;

    private final JobApi jobApi;

    /**
     * @see CoreApi#initialize()
     */
    @Override
    public InitializeVo initialize() {
        InitializeVo.InitializeVoBuilder builder = InitializeVo.builder()
            .loginCaptchaEnabled(this.configApi.getBoolean(ConfigBizTypeEnum.LOGIN_CAPTCHA_ENABLED.getCode()));

        if (this.checkAccessLimit()) {
            builder.accessLimitEnabled(this.checkAccessLimit());
            builder.accessLimitType(this.configApi.getString(ConfigBizTypeEnum.ACCESS_LIMIT_TYPE.getCode(), "warn"));
            builder.accessLimitMessage(this.configApi.getString(ConfigBizTypeEnum.ACCESS_LIMIT_MESSAGE.getCode(), ""));
        }

        // 应用信息|站点信息
        AppVo appVo = AppVo.builder()
            .title(this.configApi.getString(ConfigBizTypeEnum.APP_TITLE.getCode(), ""))
            .copyright(this.configApi.getString(ConfigBizTypeEnum.APP_COPYRIGHT.getCode(), ""))
            .mobileDomain(this.configApi.getString(ConfigBizTypeEnum.APP_MOBILE_DOMAIN.getCode(), ""))
            .webSocketServer(this.configApi.getString(ConfigBizTypeEnum.APP_WEB_SOCKET_SERVER.getCode(), ""))
            .wxMpApp(this.wxMpApi.getWxMpApp())
            .build();
        builder.app(appVo);

        LocationConfig locationConfig = configApi.getLocationConfig();
        builder.location(locationConfig);

        return builder.build();
    }

    /**
     * @see CoreApi#checkAccessLimit()
     */
    @Override
    public PageVo getPage(String code) {
        String configKey = switch (nvl(code).toUpperCase()) {
            case "APP_CONTACT" -> ConfigBizTypeEnum.APP_CONTACT.getCode();
            case "APP_AGREEMENT_MEMBER" -> ConfigBizTypeEnum.APP_AGREEMENT_MEMBER.getCode();
            case "APP_AGREEMENT_USER" -> ConfigBizTypeEnum.APP_AGREEMENT_USER.getCode();
            case "APP_AGREEMENT_PRIVACY_POLICY" -> ConfigBizTypeEnum.APP_AGREEMENT_PRIVACY_POLICY.getCode();
            default -> ConfigBizTypeEnum.APP_TITLE.getCode();
        };
        String content = this.configApi.getString(configKey);
        return PageVo.builder().content(content).build();
    }

    /**
     * @see CoreApi#checkAccessLimit()
     */
    @Override
    public boolean checkAccessLimit() {
        boolean result = false;

        // 检测当前是否已经开启国家地区区域限制
        if (this.configApi.getBoolean(ConfigBizTypeEnum.ACCESS_LIMIT_ENABLED.getCode())) {
            String host = WebServletUtils.getIp();

            log.info("checkAccessLimit ip [{}]", host);
            List<String> countryList = Lists.newArrayList();
            String countryConfig = this.configApi.getString(ConfigBizTypeEnum.ACCESS_LIMIT_COUNTRY.getCode());
            if (StringUtils.isNotEmpty(countryConfig)) {
                countryList.addAll(Arrays.stream(countryConfig.split(GlobalConstants.STR_DELIMITER)).toList());
            }

            try {
                Ip ip = GlobalIpManager.getGeoLite().search(host);
                String country = ip.getCountry().getCode();
                log.info("checkAccessLimit ip [{}] country [{}]", host, country);
                result = CollectionUtils.isNotEmpty(countryList) && countryList.stream().allMatch(country::equalsIgnoreCase);
            } catch (AddressNotFoundException e) {
                log.error("Failed to check access limit. ip [{}] not found.", host, e);
            } catch (Exception e) {
                log.error("Failed to check access limit. ip [{}]", host, e);
            }
            log.info("checkAccessLimit ip [{}] result [{}]", host, result);
        }
        return result;
    }

    /**
     * @see CoreApi#setup()
     */
    @Override
    @Transactional
    public void setup() {
        // 业务类型初始化
        bizTypeApi.initialize();
        // 系统配置项初始化
        configApi.initialize();
        // 权限信息初始化
        securityApi.initialize();
        // 消息模版初始化
        messageApi.initialize();
        // 智能体初始化
        aiToolApi.initialize();
        aiModelApi.initialize();
        aiAgentApi.initialize();
        aiKbApi.initialize();
        // 区域初始化
        regionApi.initialize();
        // 字典初始化
        dictApi.initialize();
        // 标签初始化
        tagApi.initialize();
        // 定时任务初始化
        jobApi.initialize();
    }

}
