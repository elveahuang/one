package cc.wdev.platform.security.core.oauth;

import cc.wdev.platform.system.security.api.AuthorizationConsentApi;
import cc.wdev.platform.system.security.domain.dto.AuthorizationConsentDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class CustomOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final AuthorizationConsentApi authorizationConsentApi;

    @Override
    public void save(@NonNull OAuth2AuthorizationConsent authorizationConsent) {
        this.authorizationConsentApi.save(toDto(authorizationConsent));
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        this.authorizationConsentApi.deleteByKey(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }

    @Override
    public OAuth2AuthorizationConsent findById(@NonNull String registeredClientId, @NonNull String principalName) {
        return toObject(this.authorizationConsentApi.findByKey(registeredClientId, principalName));
    }

    private OAuth2AuthorizationConsent toObject(AuthorizationConsentDto dto) {
        String registeredClientId = dto.getClientId();
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(registeredClientId, dto.getPrincipalName());
        if (dto.getAuthorities() != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(dto.getAuthorities())) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }
        return builder.build();
    }

    private AuthorizationConsentDto toDto(OAuth2AuthorizationConsent object) {
        AuthorizationConsentDto dto = new AuthorizationConsentDto();
        dto.setClientId(object.getRegisteredClientId());
        dto.setPrincipalName(object.getPrincipalName());
        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : object.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        dto.setAuthorities(StringUtils.collectionToCommaDelimitedString(authorities));
        return dto;
    }

}
