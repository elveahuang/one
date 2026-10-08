package cc.wdev.platform.commons.data.jpa.service;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.jpa.domain.BaseEntity;
import cc.wdev.platform.commons.data.jpa.domain.SimpleEntity;
import cc.wdev.platform.commons.data.jpa.repository.BaseEntityRepository;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BaseResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.service.AbstractService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.GenericsUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @param <T> 实体
 * @param <K> 主键
 * @param <R> Repository
 * @author elvea
 * @see AbstractService
 * @see EnhancedEntityService
 * @see EntityService
 */
@Slf4j
@NoRepositoryBean
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BaseEntityService<T extends IdEntity, K extends Serializable, R extends BaseEntityRepository<T, K>>
    extends AbstractService
    implements EntityService<T, K>, EnhancedEntityService<T, K, R> {

    @Autowired
    protected R repository;

    protected Class<T> entityClass = currentEntityClass();

    protected Class<K> entityIdClass = currentEntityIdClass();

    protected Class<R> repositoryClass = currentRepositoryClass();

    // -----------------------------------------------------------------------------------------------------------------
    // 辅助方法
    // -----------------------------------------------------------------------------------------------------------------

    protected Class<R> currentRepositoryClass() {
        return GenericsUtils.getSuperGenericType(getClass(), BaseEntityService.class, 2);
    }

    /**
     * @see EnhancedEntityService#getRepository()
     */
    @Override
    public R getRepository() {
        return this.repository;
    }

    /**
     * @see EnhancedEntityService#getRepositoryClass()
     */
    @Override
    public Class<R> getRepositoryClass() {
        return this.repositoryClass;
    }

    /**
     * @see EntityService#getEntityClass()
     */
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * @see EntityService#getEntityIdClass()
     */
    @Override
    public Class<K> getEntityIdClass() {
        return entityIdClass;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // EntityService
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see EntityService#checkExistsOrFail(Serializable, BaseResponseCodeEnum)
     */
    @Override
    public T checkExistsOrFail(K id, BaseResponseCodeEnum responseCode) {
        T entity = this.getRepository().findById(id).orElse(null);
        if (entity == null) {
            throw new ServiceException(responseCode);
        }
        return entity;
    }

    /**
     * @see EntityService#checkExistsOrReturn(Serializable, IdEntity, BaseResponseCodeEnum)
     */
    @Override
    public T checkExistsOrReturn(K id, @NonNull T instance, BaseResponseCodeEnum responseCode) {
        if (!ObjectUtils.isValidId(id)) {
            return instance;
        }
        T entity = this.getRepository().findById(id).orElse(null);
        if (entity == null) {
            throw new ServiceException(responseCode);
        }
        return entity;
    }

    @Override
    public void checkOperationPermission(Collection<K> ids, BaseResponseCodeEnum responseCode) {
        // 空值不判断
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 获取有效值，新增的话id无效不判断
        List<K> validIdsList = ids.stream().filter(ObjectUtils::isValidId).toList();
        if (CollectionUtils.isEmpty(validIdsList)) {
            return;
        }
        List<T> entityList = this.getRepository().findAllById(validIdsList);
        for (T entity : entityList) {
            if (entity instanceof BaseEntity) {
                Long createdBy = ((BaseEntity) entity).getCreatedBy();
                if (createdBy != null && !createdBy.equals(SecurityUtils.getUid())) {
                    throw new ServiceException(responseCode);
                }
            }
        }
    }

    /**
     * @see EntityService#findById(Serializable)
     */
    @Override
    public T findById(K id) {
        return this.getRepository().findById(id).orElse(null);
    }

    /**
     * @see EntityService#findById(Serializable, Consumer)
     */
    @Override
    public T findById(K id, Consumer<T> extraFn) {
        T entity = this.getRepository().findById(id).orElse(null);
        extraFn.accept(entity);
        return entity;
    }

    /**
     * @see EntityService#findByIds(Collection)
     */
    @Override
    public List<T> findByIds(Collection<K> ids) {
        return (CollectionUtils.isNotEmpty(ids)) ?
            CollectionUtils.toArrayList(getRepository().findAllById(ids)) : Collections.emptyList();
    }

    /**
     * @see EntityService#findAll()
     */
    @Override
    public List<T> findAll() {
        return CollectionUtils.toArrayList(this.getRepository().findAll());
    }

    /**
     * @see EntityService#findAll(Sort)
     */
    @Override
    public List<T> findAll(Sort sort) {
        return CollectionUtils.toArrayList(this.getRepository().findAll(sort));
    }

    /**
     * @see EntityService#findAll(Pageable)
     */
    @Override
    public List<T> findAll(Pageable pageable) {
        return CollectionUtils.toArrayList(this.getRepository().findAll(pageable));
    }

    /**
     * @see EntityService#findAll(Pageable)
     */
    @Override
    public Page<T> findByPage(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    /**
     * @see EntityService#findAll(Pageable)
     */
    @Override
    public Page<T> findByPage(Pageable pageable, T example) {
        if (example == null) {
            return findByPage(pageable);
        }
        return this.getRepository().findAll(Example.of(example), pageable);
    }

    /**
     * @see EntityService#insert(IdEntity)
     */
    @Override
    public T insert(T entity) {
        return this.save(entity);
    }

    /**
     * @see EntityService#insertBatch(Collection, int)
     */
    @Override
    public void insertBatch(Collection<T> entityList, int batchSize) {
        this.getRepository().saveAll(entityList);
    }

    /**
     * @see EntityService#updateById(IdEntity)
     */
    @Override
    public T updateById(T entity) {
        return this.save(entity);
    }

    /**
     * @see EntityService#updateBatchById(Collection, int)
     */
    @Override
    public void updateBatchById(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            this.getRepository().saveAllAndFlush(entityList);
        }
    }

    /**
     * @see EntityService#save(IdEntity)
     */
    @Override
    @Transactional
    public T save(T entity) {
        return this.getRepository().saveAndFlush(entity);
    }

    /**
     * @see EntityService#saveBatch(Collection, int)
     */
    @Override
    public void saveBatch(Collection<T> entityList, int batchSize) {
        CollectionUtils.toArrayList(this.getRepository().saveAllAndFlush(entityList));
    }

    /**
     * @see EntityService#deleteById(Serializable)
     */
    @Override
    public void deleteById(K id) {
        this.getRepository().deleteById(id);
    }

    /**
     * @see EntityService#deleteBatchById(Collection)
     */
    @Override
    public void deleteBatchById(Collection<K> entityIdList) {
        if (CollectionUtils.isNotEmpty(entityIdList)) {
            this.getRepository().deleteAllById(entityIdList);
        }
    }

    /**
     * @see EntityService#delete(IdEntity)
     */
    @Override
    public void delete(T entity) {
        this.getRepository().delete(entity);
    }

    /**
     * @see EntityService#deleteBatch(Collection, int)
     */
    @Override
    public void deleteBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            this.getRepository().deleteAll(entityList);
        }
    }

    /**
     * @see EntityService#deleteAll()
     */
    @Override
    public void deleteAll() {
        this.getRepository().deleteAll();
    }

    /**
     * @see EntityService#softDeleteBatch(Collection, int)
     */
    @Override
    public void softDeleteBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            // 批量软删除实体 - JPA
            this.updateBatchById(entityList.stream().peek(e -> {
                if (e instanceof cc.wdev.platform.commons.data.jpa.domain.BaseEntity entity) {
                    entity.setActive(ActiveTypeEnum.DISABLED.getValue());
                    entity.setDeletedAt(getCurLocalDateTime());
                    entity.setDeletedBy(SecurityUtils.getUid());
                } else if (e instanceof cc.wdev.platform.commons.data.jpa.domain.SimpleEntity entity) {
                    entity.setActive(ActiveTypeEnum.DISABLED.getValue());
                }
            }).toList(), batchSize);
        }
    }

    /**
     * @see EntityService#softDelete(IdEntity)
     */
    @Override
    public void softDelete(T entity) {
        if (entity instanceof BaseEntity baseEntity) {
            // 批量软删除实体 - Jpa BaseEntity
            baseEntity.setDeletedAt(getCurLocalDateTime());
            baseEntity.setDeletedBy(SecurityUtils.getUid());
            baseEntity.setActive(ActiveTypeEnum.DISABLED.getValue());
            this.save(entity);
        } else if (entity instanceof SimpleEntity simpleEntity) {
            // 批量软删除实体 - Jpa SimpleEntity
            simpleEntity.setActive(ActiveTypeEnum.DISABLED.getValue());
            this.save(entity);
        }
    }

    /**
     * @see EntityService#count()
     */
    @Override
    public long count() {
        return this.getRepository().count();
    }

    /**
     * @see EntityService#existsById(Serializable)
     */
    @Override
    public boolean existsById(K id) {
        return this.getRepository().existsById(id);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // EnhancedEntityService
    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    // 辅助方法
    // -----------------------------------------------------------------------------------------------------------------

}
