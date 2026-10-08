package cc.wdev.platform.system.security.service.impl;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.security.cache.AuthorizationCacheKeyGenerator;
import cc.wdev.platform.system.security.domain.entity.AuthorizationEntity;
import cc.wdev.platform.system.security.repository.AuthorizationRepository;
import cc.wdev.platform.system.security.service.AuthorizationService;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 * @see AuthorizationService
 */
@Service
public class AuthorizationServiceImpl extends BaseCachingEntityService<AuthorizationEntity, Long, AuthorizationRepository> implements AuthorizationService {

    private final AuthorizationCacheKeyGenerator cacheKeyGenerator = new AuthorizationCacheKeyGenerator();

    @Override
    public AuthorizationCacheKeyGenerator getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    @Override
    public void updateByUuid(AuthorizationEntity entity) {
        long count = this.lambdaQueryWrapper().eq(AuthorizationEntity::getUuid, entity.getUuid()).count();
        if (count > 0) {
            this.lambdaUpdateWrapper().eq(AuthorizationEntity::getUuid, entity.getUuid()).update(entity);
            this.deleteCache(entity);
        } else {
            this.save(entity);
        }
    }

    @Override
    public void deleteByUuid(String uuid) {
        AuthorizationEntity entity = this.findByUuid(uuid);
        if (ObjectUtils.isEmpty(entity)) {
            return;
        }
        this.softDelete(entity);
    }

    @Override
    public AuthorizationEntity findByUuid(String uuid) {
        return getCacheService().get(getCacheKeyGenerator().byUuid(uuid), k -> lambdaQueryWrapper()
            .eq(AuthorizationEntity::getUuid, uuid)
            .one()
        );
    }

    @Override
    public AuthorizationEntity findByState(String state) {
        return getCacheService().get(getCacheKeyGenerator().keyByState(state), k -> lambdaQueryWrapper()
            .eq(AuthorizationEntity::getState, state)
            .one()
        );
    }

    @Override
    public AuthorizationEntity findByAuthorizationCodeValue(String authorizationCodeValue) {
        return getCacheService().get(getCacheKeyGenerator().keyByAuthorizationCodeValue(authorizationCodeValue), k -> lambdaQueryWrapper()
            .eq(AuthorizationEntity::getAuthorizationCodeValue, authorizationCodeValue)
            .one()
        );
    }

    @Override
    public AuthorizationEntity findByOidcIdTokenValue(String oidcIdTokenValue) {
        return getCacheService().get(getCacheKeyGenerator().keyByOidcIdTokenValue(oidcIdTokenValue), k -> lambdaQueryWrapper()
            .eq(AuthorizationEntity::getOidcIdTokenValue, oidcIdTokenValue)
            .one()
        );
    }

    @Override
    public AuthorizationEntity findByAccessTokenValue(String accessTokenValue) {
        return getCacheService().get(getCacheKeyGenerator().keyByAccessTokenValue(accessTokenValue), k -> lambdaQueryWrapper()
            .eq(AuthorizationEntity::getAccessTokenValue, accessTokenValue)
            .one()
        );
    }

    @Override
    public AuthorizationEntity findByRefreshTokenValue(String refreshTokenValue) {
        return getCacheService().get(getCacheKeyGenerator().keyByRefreshTokenValue(refreshTokenValue), k -> lambdaQueryWrapper()
            .eq(AuthorizationEntity::getRefreshTokenValue, refreshTokenValue)
            .one()
        );
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(AuthorizationEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(getCacheKeyGenerator().byId(model.getId()), model);
            }
            if (StringUtils.isNotEmpty(model.getUuid())) {
                getCacheService().set(getCacheKeyGenerator().byUuid(model.getUuid()), model);
            }
            if (StringUtils.isNotEmpty(model.getState())) {
                getCacheService().set(getCacheKeyGenerator().keyByState(model.getState()), model);
            }
            if (StringUtils.isNotEmpty(model.getAuthorizationCodeValue())) {
                getCacheService().set(getCacheKeyGenerator().keyByAuthorizationCodeValue(model.getAuthorizationCodeValue()), model);
            }
            if (StringUtils.isNotEmpty(model.getOidcIdTokenValue())) {
                getCacheService().set(getCacheKeyGenerator().keyByOidcIdTokenValue(model.getOidcIdTokenValue()), model);
            }
            if (StringUtils.isNotEmpty(model.getAccessTokenValue())) {
                getCacheService().set(getCacheKeyGenerator().keyByAccessTokenValue(model.getAccessTokenValue()), model);
            }
            if (StringUtils.isNotEmpty(model.getRefreshTokenValue())) {
                getCacheService().set(getCacheKeyGenerator().keyByRefreshTokenValue(model.getRefreshTokenValue()), model);
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(AuthorizationEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(getCacheKeyGenerator().byId(model.getId()));
            }
            if (StringUtils.isNotEmpty(model.getUuid())) {
                getCacheService().delete(getCacheKeyGenerator().byUuid(model.getUuid()));
            }
            if (StringUtils.isNotEmpty(model.getState())) {
                getCacheService().delete(getCacheKeyGenerator().keyByState(model.getState()));
            }
            if (StringUtils.isNotEmpty(model.getAuthorizationCodeValue())) {
                getCacheService().delete(getCacheKeyGenerator().keyByAuthorizationCodeValue(model.getAuthorizationCodeValue()));
            }
            if (StringUtils.isNotEmpty(model.getOidcIdTokenValue())) {
                getCacheService().delete(getCacheKeyGenerator().keyByOidcIdTokenValue(model.getOidcIdTokenValue()));
            }
            if (StringUtils.isNotEmpty(model.getAccessTokenValue())) {
                getCacheService().delete(getCacheKeyGenerator().keyByAccessTokenValue(model.getAccessTokenValue()));
            }
            if (StringUtils.isNotEmpty(model.getRefreshTokenValue())) {
                getCacheService().delete(getCacheKeyGenerator().keyByRefreshTokenValue(model.getRefreshTokenValue()));
            }
        }
    }

}
