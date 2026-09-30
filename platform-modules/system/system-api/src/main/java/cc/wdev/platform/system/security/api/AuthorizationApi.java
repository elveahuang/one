package cc.wdev.platform.system.security.api;

import cc.wdev.platform.system.security.domain.dto.AuthorizationDto;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/authorization")
public interface AuthorizationApi {

    @PostExchange(value = "/save")
    void save(@RequestBody AuthorizationDto saveDto);

    @PostExchange("/delete-by-id")
    void deleteById(@Parameter(description = "ID") @RequestParam("id") Long id);

    @PostExchange("/delete-by-uuid")
    void deleteByUuid(@Parameter(description = "UUID") @RequestParam("uuid") String uuid);

    @PostExchange("/find-by-id")
    AuthorizationDto findById(@Parameter(description = "ID") @RequestParam("id") Long id);

    @PostExchange("/find-by-uuid")
    AuthorizationDto findByUuid(@Parameter(description = "UUID") @RequestParam("uuid") String uuid);

    @PostExchange("/find-by-state")
    AuthorizationDto findByState(@Parameter(description = "状态") @RequestParam("state") String state);

    @PostExchange("/find-by-authorization-code-value")
    AuthorizationDto findByAuthorizationCodeValue(@Parameter(description = "授权码") @RequestParam("code") String code);

    @PostExchange("/find-by-oidc-id-token-value")
    AuthorizationDto findByOidcIdTokenValue(@Parameter(description = "ID令牌") @RequestParam("token") String token);

    @PostExchange("/find-by-access-token-value")
    AuthorizationDto findByAccessTokenValue(@Parameter(description = "访问令牌") @RequestParam("token") String token);

    @PostExchange("/find-by-refresh-token-value")
    AuthorizationDto findByRefreshTokenValue(@Parameter(description = "刷新令牌") @RequestParam("token") String token);

}
