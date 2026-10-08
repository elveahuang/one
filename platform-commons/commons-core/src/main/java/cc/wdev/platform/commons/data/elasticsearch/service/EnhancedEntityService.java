package cc.wdev.platform.commons.data.elasticsearch.service;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.elasticsearch.repository.BaseEntityRepository;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.service.Service;

import java.io.Serializable;

/**
 * 增强实体服务接口
 *
 * @param <T> 实体类型
 * @param <K> 主键类型
 * @author elvea
 * @see EntityService
 * @see Service
 */
public interface EnhancedEntityService<T extends IdEntity, K extends Serializable, R extends BaseEntityRepository<T, K>>
    extends EntityService<T, K> {

    R getRepository();

    Class<R> getRepositoryClass();

}
