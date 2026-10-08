package cc.wdev.platform.system.open.api;

import cc.wdev.platform.system.open.domain.form.DingtalkConfigForm;
import cc.wdev.platform.system.open.domain.vo.DingtalkAppVo;
import cc.wdev.platform.system.open.service.DingtalkAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 钉钉应用 API 实现
 */
@Service
@RequiredArgsConstructor
public class DingtalkApiImpl implements DingtalkApi {

    private final DingtalkAppService dingtalkAppService;

    /**
     * @see DingtalkApi#getDingtalkApp()
     */
    @Override
    public DingtalkAppVo getDingtalkApp() {
        return dingtalkAppService.getDingtalkApp();
    }

    /**
     * @see DingtalkApi#getDingtalkConfig()
     */
    @Override
    public DingtalkConfigForm getDingtalkConfig() {
        return dingtalkAppService.getDingtalkConfig();
    }

    /**
     * @see DingtalkApi#saveDingtalkConfig(DingtalkConfigForm)
     */
    @Override
    public void saveDingtalkConfig(DingtalkConfigForm form) {
        dingtalkAppService.saveDingtalkConfig(form);
    }

}
