package cc.wdev.platform.commons.data.mybatis.service;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.service.Service;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;

import java.io.Serializable;
import java.util.List;

/**
 * 增强实体服务接口
 *
 * @param <T> 实体类型
 * @param <K> 主键类型
 * @author elvea
 * @see EntityService
 * @see Service
 */
public interface EnhancedEntityService<T extends IdEntity, K extends Serializable, M extends BaseEntityRepository<T, K>>
    extends EntityService<T, K> {

    M getMapper();

    Class<M> getMapperClass();

    /**
     * 查询单条记录
     */
    T findOne(QueryWrapper<T> wrapper);

    /**
     * 查询单条记录
     */
    T findOne(LambdaQueryChainWrapper<T> wrapper);

    /**
     * 查询多条记录
     */
    List<T> findList(LambdaQueryChainWrapper<T> wrapper);

    /**
     * 查询所有记录，支持分页
     *
     * @return Iterable<T>
     */
    List<T> findList(IPage<T> page);

    /**
     * 分页查询记录
     */
    IPage<T> findPage(IPage<T> page, LambdaQueryChainWrapper<T> wrapper);

    /**
     * 查询所有记录，支持分页
     *
     * @return IPage<T>
     */
    IPage<T> findPage(IPage<T> page);

    /**
     * 查询所有记录，支持分页
     *
     * @return IPage<T>
     */
    IPage<T> findPage(IPage<T> page, Wrapper<T> wrapper);

    /**
     * 检查记录是否存在
     */
    boolean checkExists(LambdaQueryChainWrapper<T> wrapper);

}
