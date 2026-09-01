package cc.wdev.platform.system.open.api;

import cc.wdev.platform.commons.oapis.weixin.config.AppCpConfig;
import cc.wdev.platform.commons.oapis.weixin.service.WxCpManager;
import cc.wdev.platform.system.open.domain.form.WxCpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxCpAppVo;
import cc.wdev.platform.system.open.service.WxCpAppService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.cp.api.WxCpService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * 企业微信应用 API 实现
 */
@Service
@RequiredArgsConstructor
public class WxCpApiImpl implements WxCpApi {

    private final ObjectProvider<WxCpManager> wxCpManager;

    private final WxCpAppService wxCpAppService;

    /**
     * @see WxCpApi#getService()
     */
    @Override
    public WxCpService getService() {
        return this.wxCpManager.getIfAvailable().getService(this.getAppConfig());
    }

    /**
     * @see WxCpApi#getAppConfig()
     */
    @Override
    public AppCpConfig getAppConfig() {
        return this.wxCpAppService.getAppConfig();
    }

    /**
     * @see WxCpApi#getWxCpApp()
     */
    @Override
    public WxCpAppVo getWxCpApp() {
        return wxCpAppService.getWxCpApp();
    }

    /**
     * @see WxCpApi#getWxCpConfig()
     */
    @Override
    public WxCpConfigForm getWxCpConfig() {
        return wxCpAppService.getWxCpConfig();
    }

    /**
     * @see WxCpApi#saveWxCpConfig(WxCpConfigForm)
     */
    @Override
    public void saveWxCpConfig(WxCpConfigForm form) {
        wxCpAppService.saveWxCpConfig(form);
    }

}
