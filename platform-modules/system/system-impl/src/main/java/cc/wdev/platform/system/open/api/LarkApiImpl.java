package cc.wdev.platform.system.open.api;

import cc.wdev.platform.system.open.domain.form.LarkConfigForm;
import cc.wdev.platform.system.open.domain.vo.LarkAppVo;
import cc.wdev.platform.system.open.service.LarkAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 飞书应用 API 实现
 */
@Service
@RequiredArgsConstructor
public class LarkApiImpl implements LarkApi {

    private final LarkAppService larkAppService;

    /**
     * @see LarkApi#getLarkApp()
     */
    @Override
    public LarkAppVo getLarkApp() {
        return larkAppService.getLarkApp();
    }

    /**
     * @see LarkApi#getLarkConfig()
     */
    @Override
    public LarkConfigForm getLarkConfig() {
        return larkAppService.getLarkConfig();
    }

    /**
     * @see LarkApi#saveLarkConfig(LarkConfigForm)
     */
    @Override
    public void saveLarkConfig(LarkConfigForm form) {
        larkAppService.saveLarkConfig(form);
    }

}
