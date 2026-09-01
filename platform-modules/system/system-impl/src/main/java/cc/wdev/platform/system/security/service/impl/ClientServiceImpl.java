package cc.wdev.platform.system.security.service.impl;

import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.security.domain.converter.ClientConverter;
import cc.wdev.platform.system.security.domain.entity.ClientEntity;
import cc.wdev.platform.system.security.domain.form.ClientForm;
import cc.wdev.platform.system.security.domain.request.ClientCheckRequest;
import cc.wdev.platform.system.security.domain.request.ClientRequest;
import cc.wdev.platform.system.security.repository.ClientRepository;
import cc.wdev.platform.system.security.service.ClientService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.CLIENT;

/**
 * @author elvea
 * @see ClientService
 */
@Slf4j
@Service
public class ClientServiceImpl extends BaseCachingEntityService<ClientEntity, Long, ClientRepository> implements ClientService {

    private final SimpleCacheKeyGenerator cacheKeyGenerator = new SimpleCacheKeyGenerator(CLIENT);

    @Override
    public SimpleCacheKeyGenerator getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    /**
     * @see ClientService#findClientById(Long)
     */
    @Override
    public ClientEntity findClientById(Long id) {
        return this.findCacheById(id);
    }

    /**
     * @see ClientService#findClientByClientId(String)
     */
    @Override
    public ClientEntity findClientByClientId(String clientId) {
        return getCacheService().get(getCacheKeyGenerator().byCode(clientId), k -> lambdaQueryWrapper()
            .eq(ClientEntity::getClientId, clientId)
            .one()
        );
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(ClientEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(getCacheKeyGenerator().byId(model.getId()), model);
            }
            if (StringUtils.isNotEmpty(model.getClientId())) {
                getCacheService().set(getCacheKeyGenerator().byCode(model.getClientId()), model);
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(ClientEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(getCacheKeyGenerator().byId(model.getId()));
            }
            if (StringUtils.isNotEmpty(model.getClientId())) {
                getCacheService().delete(getCacheKeyGenerator().byCode(model.getClientId()));
            }
        }
    }

    /**
     * 获取客户端搜索列表
     */
    @Override
    public Page<ClientEntity> findClientList(ClientRequest request) {
        IPage<ClientEntity> page = StringUtils.isNotEmpty(request.getQ()) ?
            this.lambdaQueryWrapper().eq(ClientEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .like(ClientEntity::getClientId, request.getQ())
                .page(getMyBatisPlusPage(request.getPageable()))
            :
            this.lambdaQueryWrapper()
                .eq(ClientEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .page(getMyBatisPlusPage(request.getPageable()));

        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * 保存客户端
     */
    @Override
    public Boolean saveClient(ClientForm form) {
        ClientEntity entity;
        if (null != form.getId() && form.getId() > 0) {
            entity = this.findById(form.getId());
            ObjectUtils.copyNotNullProperties(form, entity);
        } else {
            entity = ClientConverter.INSTANCE.formToEntity(form);
        }
        save(entity);

        return true;
    }

    /**
     * 删除客户端
     */
    @Override
    public Boolean deleteClient(DeleteRequest request) {
        if (null != request && null != request.getIds()) {
            for (Long id : request.getIds()) {
                // 逻辑删除
                softDeleteById(id);
            }
        }
        return true;
    }

    /**
     * 检查客户端是否重复
     */
    @Override
    public Boolean checkClient(ClientCheckRequest request) {
        return !lambdaQueryWrapper()
            .ne(existsById(request.getId()), ClientEntity::getId, request.getId())
            .eq(ClientEntity::getClientId, request.getClientId())
            .eq(ClientEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .exists();
    }

}
