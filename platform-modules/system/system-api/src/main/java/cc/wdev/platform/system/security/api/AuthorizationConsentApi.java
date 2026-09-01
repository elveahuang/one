package cc.wdev.platform.system.security.api;

import cc.wdev.platform.system.security.domain.dto.AuthorizationConsentDto;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/authorization-consent")
public interface AuthorizationConsentApi {

    @PostExchange("/save")
    void save(@RequestBody AuthorizationConsentDto saveDto);

    @PostExchange("/delete-by-key")
    void deleteByKey(@Parameter(description = "客户端ID") @RequestParam("clientId") String clientId,
                     @Parameter(description = "主体名称") @RequestParam("principalName") String principalName);

    @PostExchange("/find-by-key")
    AuthorizationConsentDto findByKey(@Parameter(description = "客户端ID") @RequestParam("clientId") String clientId,
                                      @Parameter(description = "主体名称") @RequestParam("principalName") String principalName);

}
