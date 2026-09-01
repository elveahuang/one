package cc.wdev.platform.system.security.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.security.domain.entity.ClientEntity;
import cc.wdev.platform.system.security.domain.form.ClientForm;
import cc.wdev.platform.system.security.domain.request.ClientCheckRequest;
import cc.wdev.platform.system.security.domain.request.ClientRequest;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 * @see EntityService
 * @see CachingEntityService
 */
public interface ClientService extends CachingEntityService<ClientEntity, Long> {

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return {@link ClientEntity}
     */
    ClientEntity findClientById(Long id);

    /**
     * 根据客户端标识查询
     *
     * @param clientId 客户端标识
     * @return {@link ClientEntity}
     */
    ClientEntity findClientByClientId(String clientId);

    /**
     * 获取客户端搜索列表
     */
    Page<ClientEntity> findClientList(ClientRequest request);

    /**
     * 保存客户端
     */
    Boolean saveClient(ClientForm form);

    /**
     * 删除客户端
     */
    Boolean deleteClient(DeleteRequest request);

    /**
     * 检查客户端是否重复
     */
    Boolean checkClient(ClientCheckRequest request);

}
