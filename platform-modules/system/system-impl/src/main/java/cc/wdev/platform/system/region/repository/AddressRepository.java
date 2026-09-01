package cc.wdev.platform.system.region.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.region.domain.entity.AddressEntity;
import cc.wdev.platform.system.region.domain.request.AddressSearchRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author erden
 */
@Mapper
public interface AddressRepository extends BaseEntityRepository<AddressEntity, Long> {

    /**
     * 关联业务查询地址
     */
    IPage<AddressEntity> findPageByRelation(Page<?> page, @Param("request") AddressSearchRequest request);

}
