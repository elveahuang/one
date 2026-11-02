package cc.wdev.platform.system.security.api.impl;

import cc.wdev.platform.system.security.domain.dto.ClientDto;
import cc.wdev.platform.system.security.domain.form.ClientForm;
import cc.wdev.platform.system.security.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * OAuth2 客户端保存（RegisteredClientRepository.save 语义）单元测试
 *
 * <p>回归背景：框架在客户端认证流程中会回存客户端（口令编码升级等），回存的对象不携带数据库主键。
 * 若按插入处理会产生同 clientId 的重复行，之后 findClientByClientId 的 selectOne 直接抛
 * TooManyResultsException（真机验证时第二次登录起 500）。这里锁定 upsert 与密钥保留语义。</p>
 *
 * @author elvea
 */
class ClientApiImplTests {

    private static ClientDto dto(Long id, String clientId, String secret) {
        ClientDto dto = new ClientDto();
        dto.setId(id);
        dto.setClientId(clientId);
        dto.setClientSecret(secret);
        dto.setClientName(clientId);
        return dto;
    }

    @Test
    void testNullDtoIsIgnored() {
        ClientService service = Mockito.mock(ClientService.class);
        new ClientApiImpl(service).save(null);

        Mockito.verifyNoInteractions(service);
    }

    /**
     * 已存在同 clientId 时：必须复用库内主键（更新而不是插入）
     */
    @Test
    void testExistingClientReusesId() {
        ClientService service = Mockito.mock(ClientService.class);
        ClientDto existing = dto(1001L, "webapp", "{bcrypt}$2a$10$oldHash");

        ClientApiImpl api = new ClientApiImpl(service) {
            @Override
            public ClientDto findByClientId(String clientId) {
                return existing;
            }
        };

        api.save(dto(null, "webapp", "{bcrypt}$2a$10$newHash"));

        ArgumentCaptor<ClientForm> captor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(service).saveClient(captor.capture());
        ClientForm saved = captor.getValue();

        Assertions.assertEquals(1001L, saved.getId(), "未复用库内主键，会产生重复客户端行");
    }

    /**
     * 框架回存时会把密钥重新编码，必须保留库内既有密钥，避免登录凭据被改掉
     */
    @Test
    void testExistingSecretIsPreserved() {
        ClientService service = Mockito.mock(ClientService.class);
        ClientDto existing = dto(1001L, "webapp", "{noop}c4e859c6");

        ClientApiImpl api = new ClientApiImpl(service) {
            @Override
            public ClientDto findByClientId(String clientId) {
                return existing;
            }
        };

        api.save(dto(null, "webapp", "{bcrypt}$2a$10$reencoded"));

        ArgumentCaptor<ClientForm> captor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(service).saveClient(captor.capture());

        Assertions.assertEquals("{noop}c4e859c6", captor.getValue().getClientSecret(),
            "库内既有密钥被框架回存的重编码值覆盖");
    }

    /**
     * 不存在时按插入处理（动态客户端注册场景）
     */
    @Test
    void testNewClientIsInserted() {
        ClientService service = Mockito.mock(ClientService.class);

        ClientApiImpl api = new ClientApiImpl(service) {
            @Override
            public ClientDto findByClientId(String clientId) {
                return null;
            }
        };

        api.save(dto(null, "new-client", "{noop}secret"));

        ArgumentCaptor<ClientForm> captor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(service).saveClient(captor.capture());

        Assertions.assertNull(captor.getValue().getId());
        Assertions.assertEquals("new-client", captor.getValue().getClientId());
        Assertions.assertEquals("{noop}secret", captor.getValue().getClientSecret());
    }

}
