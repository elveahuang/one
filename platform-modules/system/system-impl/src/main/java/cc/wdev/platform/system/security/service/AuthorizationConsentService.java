package cc.wdev.platform.system.security.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.security.domain.entity.AuthorizationConsentEntity;

/**
 * @author elvea
 * @see EntityService
 * @see CachingEntityService
 */
public interface AuthorizationConsentService extends CachingEntityService<AuthorizationConsentEntity, Long> {

    AuthorizationConsentEntity findByKey(String clientId, String principalName);

    void deleteByKey(String clientId, String principalName);

}
