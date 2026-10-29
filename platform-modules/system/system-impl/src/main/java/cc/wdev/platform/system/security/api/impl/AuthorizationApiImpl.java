package cc.wdev.platform.system.security.api.impl;

import cc.wdev.platform.system.security.api.AuthorizationApi;
import cc.wdev.platform.system.security.domain.converter.AuthorizationConverter;
import cc.wdev.platform.system.security.domain.dto.AuthorizationDto;
import cc.wdev.platform.system.security.domain.entity.AuthorizationEntity;
import cc.wdev.platform.system.security.service.AuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Service
@AllArgsConstructor
public class AuthorizationApiImpl implements AuthorizationApi {

    private final AuthorizationService authorizationService;

    /**
     * @see AuthorizationApi#save(AuthorizationDto)
     */
    @Override
    public void save(AuthorizationDto dto) {
        AuthorizationEntity entity = AuthorizationConverter.INSTANCE.dto2Entity(dto);
        this.authorizationService.updateByUid(entity);
    }

    /**
     * @see AuthorizationApi#deleteById(Long)
     */
    @Override
    public void deleteById(Long id) {
        this.authorizationService.deleteById(id);
    }

    /**
     * @see AuthorizationApi#deleteByUid(String)
     */
    @Override
    public void deleteByUid(String uid) {
        this.authorizationService.deleteByUid(uid);
    }

    /**
     * @see AuthorizationApi#findById(Long)
     */
    @Override
    public AuthorizationDto findById(Long id) {
        AuthorizationEntity entity = this.authorizationService.findById(id);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see AuthorizationApi#findByUid(String)
     */
    @Override
    public AuthorizationDto findByUid(String uid) {
        AuthorizationEntity entity = this.authorizationService.findByUid(uid);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see AuthorizationApi#findByState(String)
     */
    @Override
    public AuthorizationDto findByState(String state) {
        AuthorizationEntity entity = this.authorizationService.findByState(state);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see AuthorizationApi#findByAuthorizationCodeValue(String)
     */
    @Override
    public AuthorizationDto findByAuthorizationCodeValue(String code) {
        AuthorizationEntity entity = this.authorizationService.findByAuthorizationCodeValue(code);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see AuthorizationApi#findByOidcIdTokenValue(String)
     */
    @Override
    public AuthorizationDto findByOidcIdTokenValue(String token) {
        AuthorizationEntity entity = this.authorizationService.findByOidcIdTokenValue(token);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see AuthorizationApi#findByAccessTokenValue(String)
     */
    @Override
    public AuthorizationDto findByAccessTokenValue(String token) {
        AuthorizationEntity entity = this.authorizationService.findByAccessTokenValue(token);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see AuthorizationApi#findByRefreshTokenValue(String)
     */
    @Override
    public AuthorizationDto findByRefreshTokenValue(String token) {
        AuthorizationEntity entity = this.authorizationService.findByRefreshTokenValue(token);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

}
