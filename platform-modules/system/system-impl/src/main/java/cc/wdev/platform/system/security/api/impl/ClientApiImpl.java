package cc.wdev.platform.system.security.api.impl;

import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.security.api.ClientApi;
import cc.wdev.platform.system.security.domain.converter.ClientConverter;
import cc.wdev.platform.system.security.domain.dto.ClientDto;
import cc.wdev.platform.system.security.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Service
@AllArgsConstructor
public class ClientApiImpl implements ClientApi {

    private final ClientService clientService;

    /**
     * @see ClientApi#save(ClientDto)
     */
    @Override
    public void save(ClientDto clientDto) {
        if (ObjectUtils.isEmpty(clientDto)) {
            return;
        }
        // RegisteredClientRepository.save 的语义是按 clientId upsert：dto 不携带数据库主键，
        // 框架在客户端认证流程中会回存客户端（如口令编码升级），盲插会产生重复行导致
        // findClientByClientId 的 selectOne 抛 TooManyResultsException；
        // 更新分支保留库内既有密钥，避免框架回存的重新编码值覆盖原密钥
        ClientDto existing = this.findByClientId(clientDto.getClientId());
        if (existing != null && existing.getId() != null) {
            clientDto.setId(existing.getId());
            clientDto.setClientSecret(existing.getClientSecret());
        }
        this.clientService.saveClient(ClientConverter.INSTANCE.dto2Form(clientDto));
    }

    /**
     * @see ClientApi#findById(Long)
     */
    @Override
    public ClientDto findById(Long id) {
        return ClientConverter.INSTANCE.entity2Dto(this.clientService.findById(id));
    }

    /**
     * @see ClientApi#findByClientId(String)
     */
    @Override
    public ClientDto findByClientId(String clientId) {
        return ClientConverter.INSTANCE.entity2Dto(this.clientService.findClientByClientId(clientId));
    }

}
