package cc.wdev.platform.commons.data.mybatis.service;

import cc.wdev.platform.commons.data.core.domain.ActiveEntity;
import cc.wdev.platform.commons.data.core.domain.CodeEntity;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.core.domain.TitleEntity;
import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import cc.wdev.platform.commons.data.mybatis.domain.SimpleEntity;
import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BaseResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.service.AbstractService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.commons.web.request.PageRequest;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.toSpringDataPage;
import static cc.wdev.platform.commons.utils.StringUtils.isNotEmpty;

/**
 * @param <T> 实体
 * @param <K> 主键
 * @param <M> Mapper
 * @author elvea
 * @see AbstractService
 * @see EnhancedEntityService
 * @see EntityService
 */
@Slf4j
@NoRepositoryBean
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BaseEntityService<T extends IdEntity, K extends Serializable, M extends BaseEntityRepository<T, K>>
    extends AbstractService
    implements EntityService<T, K>, EnhancedEntityService<T, K, M> {

    protected Log logger = LogFactory.getLog(getClass());

    @Autowired
    protected M mapper;

    @Autowired
    protected SqlSessionFactory sqlSessionFactory;

    protected Class<T> entityClass = currentEntityClass();

    protected Class<K> entityIdClass = currentEntityIdClass();

    protected Class<M> mapperClass = currentMapperClass();

    // -----------------------------------------------------------------------------------------------------------------
    // 辅助方法
    // -----------------------------------------------------------------------------------------------------------------

    protected Class<M> currentMapperClass() {
        return GenericsUtils.getSuperGenericType(getClass(), BaseEntityService.class, 2);
    }

    /**
     * @see EnhancedEntityService#getMapper()
     */
    @Override
    public M getMapper() {
        return mapper;
    }

    /**
     * @see EnhancedEntityService#getMapperClass()
     */
    @Override
    public Class<M> getMapperClass() {
        return mapperClass;
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
        T entity = null;
        if (ObjectUtils.isValidId(id)) {
            entity = this.getMapper().selectById(id);
        }
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
        if (ObjectUtils.isInvalidId(id)) {
            return instance;
        }
        T entity = this.getMapper().selectById(id);
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
        List<T> entityList = this.getMapper().selectByIds(validIdsList);
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
        if (ObjectUtils.isInvalidId(id)) {
            return null;
        }
        return this.getMapper().selectById(id);
    }

    /**
     * @see EntityService#findById(Serializable)
     */
    @Override
    public T findByCode(String code) {
        return findOneByWrapper(queryWrapper().eq("code", code).lambda());
    }

    /**
     * @see EntityService#findById(Serializable, Consumer)
     */
    @Override
    public T findById(K id, Consumer<T> extraFn) {
        T entity = this.getMapper().selectById(id);
        extraFn.accept(entity);
        return entity;
    }

    /**
     * @see EntityService#findByIds(Collection)
     */
    @Override
    public List<T> findByIds(Collection<K> ids) {
        return (CollectionUtils.isNotEmpty(ids)) ?
            this.getMapper().selectByIds(ids) : Collections.emptyList();
    }

    /**
     * @see EntityService#findAll()
     */
    @Override
    public List<T> findAll() {
        return getMapper().selectList(Wrappers.emptyWrapper());
    }

    /**
     * @see EntityService#findAll(Sort)
     */
    @Override
    public List<T> findAll(Sort sort) {
        return getMapper().selectList(Wrappers.emptyWrapper());
    }

    /**
     * @see EntityService#findAll(Pageable)
     */
    @Override
    public List<T> findAll(Pageable pageable) {
        return this.getMapper().selectPage(getMyBatisPlusPage(pageable), Wrappers.emptyWrapper()).getRecords();
    }

    /**
     * @see EntityService#findByPage(Pageable)
     */
    @Override
    public Page<T> findByPage(Pageable pageable) {
        return MyBatisPlusUtils.toSpringDataPage(this.getMapper().selectPage(getMyBatisPlusPage(pageable), Wrappers.emptyWrapper()));
    }

    /**
     * @see EntityService#findByPage(Pageable, IdEntity)
     */
    @Override
    public Page<T> findByPage(Pageable pageable, T example) {
        if (example == null) {
            return findByPage(pageable);
        }
        return MyBatisPlusUtils.toSpringDataPage(this.getMapper().selectPage(getMyBatisPlusPage(pageable), new QueryWrapper<>(example)));
    }

    /**
     * @see EntityService#findByPage(PageRequest)
     */
    @Override
    public Page<T> findByPage(PageRequest request) {
        QueryChainWrapper<T> wrapper = queryWrapper();
        if (getEntityClass().isAssignableFrom(ActiveEntity.class)) {
            wrapper.eq(ActiveEntity.DEFAULT_FIELD_NAME, ActiveTypeEnum.ENABLED.getValue());
        }
        if (isNotEmpty(request.getQ())) {
            List<String> fieldNames = Lists.newArrayList();
            if (ClassUtils.isAssignable(CodeEntity.class, getEntityClass())) {
                fieldNames.add(CodeEntity.DEFAULT_FIELD_NAME);
            }
            if (ClassUtils.isAssignable(TitleEntity.class, getEntityClass())) {
                fieldNames.add(TitleEntity.DEFAULT_FIELD_NAME);
            }
            if (!fieldNames.isEmpty()) {
                wrapper.and((w) -> {
                    int i = 0;
                    for (String key : fieldNames) {
                        if (i > 0) {
                            w.or();
                        }
                        w.like(key, request.getQ());
                        i++;
                    }
                });
            }
        }
        return toSpringDataPage(wrapper.page(getMyBatisPlusPage(request)));
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
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
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
            String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
            executeBatch(entityList, batchSize, (sqlSession, entity) -> {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, entity);
                return sqlSession.update(sqlStatement, param);
            });
        }
    }

    /**
     * @see EntityService#save(IdEntity)
     */
    @Override
    public T save(T entity) {
        if (ObjectUtils.isValidId(entity)) {
            getMapper().updateById(entity);
        } else {
            getMapper().insert(entity);
        }
        return entity;
    }

    /**
     * @see EntityService#saveBatch(Collection, int)
     */
    @Override
    public void saveBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        SqlHelper.saveOrUpdateBatch(this.sqlSessionFactory, this.getMapperClass(), this.logger, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                || com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), idVal));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            return sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    /**
     * @see EntityService#deleteById(Serializable)
     */
    @Override
    public void deleteById(K id) {
        this.getMapper().deleteById(id);
    }

    /**
     * @see EntityService#deleteBatchById(Collection)
     */
    @Override
    public void deleteBatchById(Collection<K> entityIdList) {
        if (CollectionUtils.isNotEmpty(entityIdList)) {
            this.getMapper().deleteByIds(entityIdList);
        }
    }

    /**
     * @see EntityService#delete(IdEntity)
     */
    @Override
    public void delete(T entity) {
        this.getMapper().deleteById(entity);
    }

    /**
     * @see EntityService#deleteBatch(Collection, int)
     */
    @Override
    public void deleteBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            this.getMapper().deleteByIds(entityList.stream().map(IdEntity::getId).collect(Collectors.toList()));
        }
    }

    /**
     * @see EntityService#deleteAll()
     */
    @Override
    public void deleteAll() {
        this.getMapper().delete(Wrappers.emptyWrapper());
    }

    /**
     * @see EntityService#softDeleteBatch(Collection, int)
     */
    @Override
    public void softDeleteBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            // 批量软删除实体 - MyBatis
            this.updateBatchById(entityList.stream().peek(e -> {
                if (e instanceof BaseEntity entity) {
                    entity.setActive(ActiveTypeEnum.DISABLED.getValue());
                    entity.setDeletedAt(getCurLocalDateTime());
                    entity.setDeletedBy(SecurityUtils.getUid());
                } else if (e instanceof SimpleEntity entity) {
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
            // 批量软删除实体 - MyBatis BaseEntity
            baseEntity.setActive(ActiveTypeEnum.DISABLED.getValue());
            baseEntity.setDeletedAt(getCurLocalDateTime());
            baseEntity.setDeletedBy(SecurityUtils.getUid());
            this.save(entity);
        } else if (entity instanceof SimpleEntity simpleEntity) {
            // 批量软删除实体 - MyBatis SimpleEntity
            simpleEntity.setActive(ActiveTypeEnum.DISABLED.getValue());
            this.save(entity);
        }
    }

    /**
     * @see EntityService#count()
     */
    @Override
    public long count() {
        return this.getMapper().selectCount(Wrappers.emptyWrapper());
    }

    /**
     * @see EntityService#existsById(Serializable)
     */
    @Override
    public boolean existsById(K id) {
        return null != id && this.findById(id) != null;
    }

    /**
     * @see EntityService#existsByCode(String, Serializable)
     */
    @Override
    public boolean existsByCode(String code, K id) {
        long total = queryWrapper()
            .eq("code", code)
            .notIn(!ObjectUtils.isEmpty(id), "id", id)
            .page(MyBatisPlusUtils.getLimitPage())
            .getTotal();
        return total > 0;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // EnhancedEntityService
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @see EnhancedEntityService#findOneByEntity(IdEntity)
     */
    @Override
    public T findOneByEntity(T example) {
        return findOneByWrapper(new QueryWrapper<>(example));
    }

    /**
     * @see EnhancedEntityService#findOneByWrapper(QueryWrapper)
     */
    @Override
    public T findOneByWrapper(QueryWrapper<T> wrapper) {
        IPage<T> page = this.mapper.selectPage(MyBatisPlusUtils.getLimitPage(), wrapper);
        if (page != null && CollectionUtils.isNotEmpty(page.getRecords())) {
            return page.getRecords().getFirst();
        }
        return null;
    }

    /**
     * @see EnhancedEntityService#findOneByWrapper(LambdaQueryChainWrapper)
     */
    @Override
    public T findOneByWrapper(LambdaQueryChainWrapper<T> wrapper) {
        List<T> entityList = wrapper.list(MyBatisPlusUtils.getLimitPage());
        if (CollectionUtils.isNotEmpty(entityList)) {
            return entityList.getFirst();
        }
        return null;
    }

    /**
     * @see EnhancedEntityService#findListByWrapper(LambdaQueryChainWrapper)
     */
    @Override
    public List<T> findListByWrapper(LambdaQueryChainWrapper<T> wrapper) {
        List<T> entityList = wrapper.list();
        return CollectionUtils.isNotEmpty(entityList) ? entityList : Collections.emptyList();
    }

    /**
     * @see EnhancedEntityService#findPageByWrapper(IPage, LambdaQueryChainWrapper)
     */
    @Override
    public IPage<T> findPageByWrapper(IPage<T> page, LambdaQueryChainWrapper<T> wrapper) {
        return wrapper.page(page);
    }

    /**
     * @see EnhancedEntityService#findAllByMpPage(IPage)
     */
    @Override
    public List<T> findAllByMpPage(IPage<T> page) {
        return this.mapper.selectList(page, Wrappers.emptyWrapper());
    }

    /**
     * @see EnhancedEntityService#findByMpPage(IPage)
     */
    @Override
    public IPage<T> findByMpPage(IPage<T> page) {
        return this.findByMpPage(page, Wrappers.emptyWrapper());
    }

    /**
     * @see EnhancedEntityService#findByMpPage(IPage, Wrapper)
     */
    @Override
    public IPage<T> findByMpPage(IPage<T> page, Wrapper<T> wrapper) {
        return this.mapper.selectPage(page, wrapper);
    }

    /**
     * @see EnhancedEntityService#checkExists(LambdaQueryChainWrapper)
     */
    @Override
    public boolean checkExists(LambdaQueryChainWrapper<T> wrapper) {
        long total = wrapper.page(MyBatisPlusUtils.getLimitPage()).getTotal();
        return total > 0;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 辅助方法
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 获取实体执行方法
     */
    protected String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(getMapperClass(), sqlMethod);
    }

    /**
     * 执行批量操作
     */
    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiFunction<SqlSession, E, Integer> consumer) {
        return SqlHelper.executeBatch(this.sqlSessionFactory, this.logger, list, batchSize, consumer);
    }

    /**
     * 执行批量操作
     */
    protected <E> boolean executeBatch(Collection<E> list, BiFunction<SqlSession, E, Integer> consumer) {
        return executeBatch(list, DEFAULT_BATCH_SIZE, consumer);
    }

    /**
     * @return QueryChainWrapper
     */
    public QueryChainWrapper<T> queryWrapper() {
        return ChainWrappers.queryChain(getMapper());
    }

    /**
     * @return LambdaQueryChainWrapper
     */
    protected LambdaQueryChainWrapper<T> lambdaQueryWrapper() {
        return ChainWrappers.lambdaQueryChain(getMapper()).setEntityClass(getEntityClass());
    }

    /**
     * @return UpdateChainWrapper
     */
    protected UpdateChainWrapper<T> updateWrapper() {
        return ChainWrappers.updateChain(getMapper());
    }

    /**
     * @return LambdaUpdateChainWrapper
     */
    protected LambdaUpdateChainWrapper<T> lambdaUpdateWrapper() {
        return ChainWrappers.lambdaUpdateChain(getMapper());
    }

}
