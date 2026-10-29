package cc.wdev.platform.system.security.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.security.domain.entity.AuthorizationEntity;

/**
 * @author elvea
 * @see EntityService
 * @see CachingEntityService
 */
public interface AuthorizationService extends CachingEntityService<AuthorizationEntity, Long> {

    void updateByUid(AuthorizationEntity entity);

    void deleteByUid(String uid);

    AuthorizationEntity findByUid(String uid);

    AuthorizationEntity findByState(String state);

    AuthorizationEntity findByAuthorizationCodeValue(String code);

    AuthorizationEntity findByOidcIdTokenValue(String token);

    AuthorizationEntity findByAccessTokenValue(String token);

    AuthorizationEntity findByRefreshTokenValue(String token);

}
