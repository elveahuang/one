package cc.wdev.platform.system.security.authentication;

import cc.wdev.platform.commons.constants.SecurityConstants;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.domain.dto.UserAuthorityDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

/**
 * @author elvea
 */
@RequiredArgsConstructor
public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserApi userApi;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Long uid = MapUtils.getLong(jwt.getClaims(), SecurityConstants.JWT_KEY_UID, 0L);
        UserAuthorityDto authorityDto = userApi.getUserAuthority(uid);
        return authorityDto.getGrantedAuthorities();
    }

}
