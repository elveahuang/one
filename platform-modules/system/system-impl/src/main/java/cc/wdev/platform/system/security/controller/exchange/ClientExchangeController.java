package cc.wdev.platform.system.security.controller.exchange;

import cc.wdev.platform.system.security.domain.converter.ClientConverter;
import cc.wdev.platform.system.security.domain.dto.ClientDto;
import cc.wdev.platform.system.security.service.ClientService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
public class ClientExchangeController {

    private final ClientService clientService;

    @PostMapping(EXCHANGE_PREFIX + "/client")
    public void save(@RequestBody ClientDto clientDto) {
    }

    @PostMapping(EXCHANGE_PREFIX + "/client/find-by-id")
    public ClientDto findById(@Parameter(description = "ID") @RequestParam("id") Long id) {
        return ClientConverter.INSTANCE.entity2Dto(this.clientService.findById(id));
    }

    @PostMapping(EXCHANGE_PREFIX + "/client/find-by-client-id")
    public ClientDto findByClientId(@Parameter(description = "客户端ID") @RequestParam("clientId") String clientId) {
        return ClientConverter.INSTANCE.entity2Dto(this.clientService.findClientByClientId(clientId));
    }

}
