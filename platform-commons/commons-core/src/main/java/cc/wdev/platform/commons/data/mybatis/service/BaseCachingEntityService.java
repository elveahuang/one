package cc.wdev.platform.commons.data.mybatis.service;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author elvea
 * @see EntityService
 * @see CachingEntityService
 * @see BaseEntityService
 */
@Slf4j
@NoRepositoryBean
public abstract class BaseCachingEntityService<T extends IdEntity, K extends Serializable, M extends BaseEntityRepository<T, K>>
    extends BaseEntityService<T, K, M> implements CachingEntityService<T, K> {

    private CacheService cacheService;

    /**
     * @see CachingEntityService#save(IdEntity)
     */
    @Override
    public T save(T entity) {
        T old = null;
        if (ObjectUtils.isValidId(entity.getId())) {
            old = getMapper().selectById(entity.getId());
        }
        T t = super.save(entity);
        deleteCache(old);
        deleteCache(t);
        return t;
    }

    /**
     * @see EntityService#deleteById(Serializable)
     */
    @Override
    public void softDelete(T entity) {
        super.softDelete(entity);
        this.deleteCache(entity);
    }

    /**
     * @see EntityService#softDeleteBatchById(Collection)
     */
    @Override
    public void softDeleteBatchById(Collection<K> entityIdList) {
        List<T> entities = this.findByIds(entityIdList);
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        super.softDeleteBatch(entities);
        for (T entity : entities) {
            this.deleteCache(entity);
        }
    }

    /**
     * @see EntityService#softDeleteBatch(Collection)
     */
    @Override
    public void softDeleteBatch(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        super.softDeleteBatch(entities);
        for (T entity : entities) {
            this.deleteCache(entity);
        }
    }

    /**
     * @see EntityService#softDeleteBatch(Collection)
     */
    @Override
    public void softDeleteBatch(Collection<T> entities, int batchSize) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        super.softDeleteBatch(entities, batchSize);
        for (T entity : entities) {
            this.deleteCache(entity);
        }
    }

    /**
     * @see EntityService#delete(IdEntity)
     */
    @Override
    public void delete(T entity) {
        super.delete(entity);
        this.deleteCache(entity);
    }

    /**
     * @see CachingEntityService#insert(IdEntity)
     */
    @Override
    public T insert(T entity) {
        T t = super.insert(entity);
        setCache(t);
        return t;
    }

    /**
     * @see CachingEntityService#updateById(IdEntity)
     */
    @Override
    public T updateById(T entity) {
        T old = null;
        if (ObjectUtils.isValidId(entity.getId())) {
            old = getMapper().selectById(entity.getId());
        }
        T t = super.updateById(entity);
        deleteCache(old);
        deleteCache(t);
        return t;
    }

    /**
     * @see CachingEntityService#saveBatch(Collection, int)
     */
    @Override
    public void saveBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            super.saveBatch(entityList, batchSize);
            entityList.forEach(this::deleteCache);
        }
    }

    /**
     * @see CachingEntityService#getCacheService()
     */
    @Override
    public CacheService getCacheService() {
        return this.cacheService;
    }

    @Autowired
    @Qualifier("cacheService")
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

}
