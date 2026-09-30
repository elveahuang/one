package cc.wdev.platform.system.security.controller.exchange;

import cc.wdev.platform.system.security.domain.dto.AuthorizationConsentDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
public class AuthorizationConsentExchangeController {

    @PostMapping(EXCHANGE_PREFIX + "/authorization-consent/save")
    public void save(@RequestBody AuthorizationConsentDto saveDto) {
    }

    @PostMapping(EXCHANGE_PREFIX + "/authorization-consent/delete-by-key")
    public void deleteByKey(@Parameter(description = "客户端ID") @RequestParam("clientId") String clientId,
                            @Parameter(description = "主体名称") @RequestParam("principalName") String principalName) {
    }

    @PostMapping(EXCHANGE_PREFIX + "/authorization-consent/find-by-key")
    public AuthorizationConsentDto findByKey(@Parameter(description = "客户端ID") @RequestParam("clientId") String clientId,
                                             @Parameter(description = "主体名称") @RequestParam("principalName") String principalName) {
        return null;
    }

}
