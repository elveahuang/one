package cc.wdev.platform.system.security.controller.exchange;

import cc.wdev.platform.system.security.domain.converter.AuthorizationConverter;
import cc.wdev.platform.system.security.domain.dto.AuthorizationDto;
import cc.wdev.platform.system.security.domain.entity.AuthorizationEntity;
import cc.wdev.platform.system.security.service.AuthorizationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(EXCHANGE_PREFIX + "/authorization")
public class AuthorizationExchangeController {

    private final AuthorizationService authorizationService;

    @PostMapping(value = "/save")
    public void save(@RequestBody AuthorizationDto dto) {
        AuthorizationEntity entity = AuthorizationConverter.INSTANCE.dto2Entity(dto);
        this.authorizationService.updateByUuid(entity);
    }

    @PostMapping("/delete-by-id")
    public void deleteById(@Parameter(description = "ID") @RequestParam("id") Long id) {
        this.authorizationService.deleteById(id);
    }

    @PostMapping("/delete-by-uuid")
    public void deleteByUuid(@Parameter(description = "UUID") @RequestParam("uuid") String uuid) {
        this.authorizationService.deleteByUuid(uuid);
    }

    @PostMapping("/find-by-id")
    public AuthorizationDto findById(@Parameter(description = "ID") @RequestParam("id") Long id) {
        AuthorizationEntity entity = this.authorizationService.findById(id);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    @PostMapping("/find-by-uuid")
    public AuthorizationDto findByUuid(@Parameter(description = "UUID") @RequestParam("uuid") String uuid) {
        AuthorizationEntity entity = this.authorizationService.findByUuid(uuid);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }


    @PostMapping("/find-by-state")
    public AuthorizationDto findByState(@Parameter(description = "状态") @RequestParam("state") String state) {
        AuthorizationEntity entity = this.authorizationService.findByState(state);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }


    @PostMapping("/find-by-authorization-code-value")
    public AuthorizationDto findByAuthorizationCodeValue(@Parameter(description = "授权码") @RequestParam("code") String code) {
        AuthorizationEntity entity = this.authorizationService.findByAuthorizationCodeValue(code);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    @PostMapping("/find-by-oidc-id-token-value")
    public AuthorizationDto findByOidcIdTokenValue(@Parameter(description = "ID令牌") @RequestParam("token") String token) {
        AuthorizationEntity entity = this.authorizationService.findByOidcIdTokenValue(token);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    @PostMapping("/find-by-access-token-value")
    public AuthorizationDto findByAccessTokenValue(@Parameter(description = "访问令牌") @RequestParam("token") String token) {
        AuthorizationEntity entity = this.authorizationService.findByAccessTokenValue(token);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

    @PostMapping("/find-by-refresh-token-value")
    public AuthorizationDto findByRefreshTokenValue(@Parameter(description = "刷新令牌") @RequestParam("token") String token) {
        AuthorizationEntity entity = this.authorizationService.findByRefreshTokenValue(token);
        return AuthorizationConverter.INSTANCE.entity2Dto(entity);
    }

}
