package cc.wdev.platform.system.open.api;

import cc.wdev.platform.system.open.domain.form.DingtalkConfigForm;
import cc.wdev.platform.system.open.domain.vo.DingtalkAppVo;

/**
 * 钉钉接口
 */
public interface DingtalkApi {

    /**
     * 获取钉钉应用信息
     */
    DingtalkAppVo getDingtalkApp();

    /**
     * 获取钉钉配置
     */
    DingtalkConfigForm getDingtalkConfig();

    /**
     * 保存钉钉配置
     */
    void saveDingtalkConfig(DingtalkConfigForm form);

}
