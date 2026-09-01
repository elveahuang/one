package cc.wdev.platform.system.open.api;

import cc.wdev.platform.system.open.domain.form.LarkConfigForm;
import cc.wdev.platform.system.open.domain.vo.LarkAppVo;

/**
 * 飞书接口
 */
public interface LarkApi {

    /**
     * 获取飞书应用信息
     */
    LarkAppVo getLarkApp();

    /**
     * 获取飞书配置
     */
    LarkConfigForm getLarkConfig();

    /**
     * 保存飞书配置
     */
    void saveLarkConfig(LarkConfigForm form);

}
