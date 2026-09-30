package cc.wdev.platform.system.open.api;

import cc.wdev.platform.commons.oapis.weixin.config.AppMpConfig;
import cc.wdev.platform.commons.oapis.weixin.service.WxMpManager;
import cc.wdev.platform.system.open.domain.form.WxMpConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMpAppVo;
import cc.wdev.platform.system.open.service.WxMpAppService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;

/**
 * 微信公众号应用 API 实现
 */
@Service
@RequiredArgsConstructor
public class WxMpApiImpl implements WxMpApi {

    private final WxMpAppService wxMpAppService;

    private final WxMpManager wxMpManager;

    /**
     * @see WxMpApi#getWxMpApp()
     */
    @Override
    public WxMpService getService() {
        return this.wxMpManager.getService(this.getAppConfig());
    }

    /**
     * @see WxMpApi#getAppConfig()
     */
    @Override
    public AppMpConfig getAppConfig() {
        return this.wxMpAppService.getAppConfig();
    }

    /**
     * @see WxMpApi#getWxMpApp()
     */
    @Override
    public WxMpAppVo getWxMpApp() {
        return wxMpAppService.getWxMpApp();
    }

    /**
     * @see WxMpApi#getWxMpConfig()
     */
    @Override
    public WxMpConfigForm getWxMpConfig() {
        return wxMpAppService.getWxMpConfig();
    }

    /**
     * @see WxMpApi#saveWxMpConfig(WxMpConfigForm)
     */
    @Override
    public void saveWxMpConfig(WxMpConfigForm form) {
        wxMpAppService.saveWxMpConfig(form);
    }

}
