package cc.wdev.platform.system.open.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.open.domain.entity.DingtalkAppEntity;
import cc.wdev.platform.system.open.domain.form.DingtalkConfigForm;
import cc.wdev.platform.system.open.domain.vo.DingtalkAppVo;

/**
 * 钉钉应用 Service
 */
public interface DingtalkAppService extends CachingEntityService<DingtalkAppEntity, Long> {

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
