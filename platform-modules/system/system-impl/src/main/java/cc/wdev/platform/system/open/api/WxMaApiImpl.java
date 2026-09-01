package cc.wdev.platform.system.open.api;

import cc.wdev.platform.system.open.domain.form.WxMaConfigForm;
import cc.wdev.platform.system.open.domain.vo.WxMaAppVo;
import cc.wdev.platform.system.open.service.WxMaAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 微信小程序应用 API 实现
 */
@Service
@RequiredArgsConstructor
public class WxMaApiImpl implements WxMaApi {

    private final WxMaAppService wxMaAppService;

    /**
     * @see WxMaApi#getWxMaConfig()
     */
    @Override
    public WxMaConfigForm getWxMaConfig() {
        return wxMaAppService.getWxMaConfig();
    }

    /**
     * @see WxMaApi#saveWxMaConfig(WxMaConfigForm)
     */
    @Override
    public void saveWxMaConfig(WxMaConfigForm form) {
        wxMaAppService.saveWxMaConfig(form);
    }

    /**
     * @see WxMaApi#getWxMaApp()
     */
    @Override
    public WxMaAppVo getWxMaApp() {
        return wxMaAppService.getWxMaApp();
    }

}
