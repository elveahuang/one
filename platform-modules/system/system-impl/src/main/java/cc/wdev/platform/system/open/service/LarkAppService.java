package cc.wdev.platform.system.open.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.open.domain.entity.LarkAppEntity;
import cc.wdev.platform.system.open.domain.form.LarkConfigForm;
import cc.wdev.platform.system.open.domain.vo.LarkAppVo;

/**
 * 飞书应用 Service
 */
public interface LarkAppService extends CachingEntityService<LarkAppEntity, Long> {

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
