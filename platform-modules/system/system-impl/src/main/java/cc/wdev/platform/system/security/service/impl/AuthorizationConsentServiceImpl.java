package cc.wdev.platform.system.security.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.security.domain.entity.AuthorizationConsentEntity;
import cc.wdev.platform.system.security.repository.AuthorizationConsentRepository;
import cc.wdev.platform.system.security.service.AuthorizationConsentService;
import org.springframework.stereotype.Service;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.AUTHORIZATION_CONSENT;

/**
 * @author elvea
 * @see AuthorizationConsentService
 */
@Service
public class AuthorizationConsentServiceImpl
    extends BaseCachingEntityService<AuthorizationConsentEntity, Long, AuthorizationConsentRepository>
    implements AuthorizationConsentService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleCacheKeyGenerator(AUTHORIZATION_CONSENT);

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    /**
     * @see AuthorizationConsentService#findByKey(String, String)
     */
    @Override
    public AuthorizationConsentEntity findByKey(String clientId, String principalName) {
        return getCacheService().get(getCacheKeyGenerator().key(clientId, principalName), _ -> lambdaQueryWrapper()
            .eq(AuthorizationConsentEntity::getClientId, clientId)
            .in(AuthorizationConsentEntity::getPrincipalName, principalName)
            .one()
        );
    }

    /**
     * @see AuthorizationConsentService#deleteByKey(String, String)
     */
    @Override
    public void deleteByKey(String clientId, String principalName) {
        CacheKey cacheKey = getCacheKeyGenerator().key(clientId, principalName);
        this.getCacheService().delete(cacheKey);
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(AuthorizationConsentEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(getCacheKeyGenerator().key(model.getClientId(), model.getPrincipalName()), model);
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(AuthorizationConsentEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(getCacheKeyGenerator().key(model.getClientId(), model.getPrincipalName()));
            }
        }
    }

}
