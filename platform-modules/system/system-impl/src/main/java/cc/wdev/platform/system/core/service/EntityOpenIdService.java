package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.core.domain.entity.EntityOpenIdEntity;

/**
 * @author erden
 */
public interface EntityOpenIdService extends CachingEntityService<EntityOpenIdEntity, Long> {

    EntityOpenIdEntity findEntityByOpenId(String bizType, String openId);

    EntityOpenIdEntity findEntityByBizId(String bizType, Long bizId);

    void saveOpenId(Long id, String bizType, SocialUser socialUser);
}
