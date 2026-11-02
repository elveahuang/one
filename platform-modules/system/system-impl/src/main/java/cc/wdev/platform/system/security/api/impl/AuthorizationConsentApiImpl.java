package cc.wdev.platform.system.security.api.impl;

import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.security.api.AuthorizationConsentApi;
import cc.wdev.platform.system.security.domain.converter.AuthorizationConsentConverter;
import cc.wdev.platform.system.security.domain.dto.AuthorizationConsentDto;
import cc.wdev.platform.system.security.domain.entity.AuthorizationConsentEntity;
import cc.wdev.platform.system.security.service.AuthorizationConsentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Service
@AllArgsConstructor
public class AuthorizationConsentApiImpl implements AuthorizationConsentApi {

    private final AuthorizationConsentService authorizationConsentService;

    /**
     * @see AuthorizationConsentApi#save(AuthorizationConsentDto)
     */
    @Override
    public void save(AuthorizationConsentDto saveDto) {
        if (ObjectUtils.isEmpty(saveDto)) {
            return;
        }
        AuthorizationConsentEntity existing = this.authorizationConsentService
            .findByKey(saveDto.getClientId(), saveDto.getPrincipalName());
        if (!ObjectUtils.isEmpty(existing)) {
            existing.setAuthorities(saveDto.getAuthorities());
            this.authorizationConsentService.updateById(existing);
        } else {
            this.authorizationConsentService.save(AuthorizationConsentConverter.INSTANCE.dto2Entity(saveDto));
        }
    }

    /**
     * @see AuthorizationConsentApi#deleteByKey(String, String)
     */
    @Override
    public void deleteByKey(String clientId, String principalName) {
        this.authorizationConsentService.deleteByKey(clientId, principalName);
    }

    /**
     * @see AuthorizationConsentApi#findByKey(String, String)
     */
    @Override
    public AuthorizationConsentDto findByKey(String clientId, String principalName) {
        return AuthorizationConsentConverter.INSTANCE
            .entity2Dto(this.authorizationConsentService.findByKey(clientId, principalName));
    }

}
