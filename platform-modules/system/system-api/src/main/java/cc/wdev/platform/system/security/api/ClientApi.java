package cc.wdev.platform.system.security.api;

import cc.wdev.platform.system.security.domain.dto.ClientDto;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/client")
public interface ClientApi {

    /**
     * 保存客户端信息
     */
    @PostExchange
    void save(@RequestBody ClientDto clientDto);

    /**
     * 获取客户端信息
     */
    @PostExchange("/find-by-id")
    ClientDto findById(@Parameter(description = "ID") @RequestParam("id") Long id);

    /**
     * 获取客户端信息
     */
    @PostExchange("/find-by-client-id")
    ClientDto findByClientId(@Parameter(description = "客户端ID") @RequestParam("clientId") String clientId);

}
