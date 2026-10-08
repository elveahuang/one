package cc.wdev.platform.system.config.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.config.domain.entity.ConfigEntity;
import cc.wdev.platform.system.config.domain.request.ConfigGetRequest;
import cc.wdev.platform.system.config.domain.request.ConfigSaveRequest;
import cc.wdev.platform.system.config.domain.request.ConfigSearchRequest;
import cc.wdev.platform.system.config.domain.vo.ConfigVo;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 */
public interface ConfigService extends CachingEntityService<ConfigEntity, Long> {

    /**
     * 保存配置
     */
    ConfigEntity getConfigEntity(ConfigGetRequest form);

    /**
     * 保存配置
     */
    void saveConfig(ConfigSaveRequest form);

    /**
     * 根据编码获取配置实体
     */
    ConfigVo getConfig(String key);

    /**
     * 根据条件获取配置列表
     */
    Page<ConfigEntity> findByPage(ConfigSearchRequest searchRequest);

}
