package cc.wdev.platform.system.security.api.impl;

import cc.wdev.platform.system.security.api.AuthorizationConsentApi;
import cc.wdev.platform.system.security.domain.dto.AuthorizationConsentDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Service
@AllArgsConstructor
public class AuthorizationConsentApiImpl implements AuthorizationConsentApi {

    /**
     * @see AuthorizationConsentApi#save(AuthorizationConsentDto)
     */
    @Override
    public void save(AuthorizationConsentDto saveDto) {
    }

    /**
     * @see AuthorizationConsentApi#deleteByKey(String, String)
     */
    @Override
    public void deleteByKey(String clientId, String principalName) {
    }

    /**
     * @see AuthorizationConsentApi#findByKey(String, String)
     */
    @Override
    public AuthorizationConsentDto findByKey(String clientId, String principalName) {
        return null;
    }

}
