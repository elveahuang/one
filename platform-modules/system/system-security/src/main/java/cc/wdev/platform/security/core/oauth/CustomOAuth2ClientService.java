package cc.wdev.platform.security.core.oauth;

import cc.wdev.platform.system.security.api.ClientApi;
import cc.wdev.platform.system.security.domain.dto.ClientDto;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import static cc.wdev.platform.security.core.utils.OAuth2Utils.toRegisteredClient;
import static cc.wdev.platform.security.core.utils.OAuth2Utils.toRegisteredClientDto;

/**
 * @author elvea
 */
@Service
@AllArgsConstructor
public class CustomOAuth2ClientService implements RegisteredClientRepository {

    private final ClientApi clientApi;

    private final TokenSettings tokenSettings;

    @Override
    public void save(RegisteredClient registeredClient) {
        this.clientApi.save(toRegisteredClientDto(registeredClient));
    }

    @Override
    public RegisteredClient findById(String id) {
        ClientDto clientDto = this.clientApi.findById(Long.valueOf(id));
        return toRegisteredClient(clientDto, tokenSettings);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        ClientDto clientDto = this.clientApi.findByClientId(clientId);
        return toRegisteredClient(clientDto, tokenSettings);
    }

}
