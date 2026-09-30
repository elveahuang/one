package cc.wdev.platform.system.site.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.site.domain.entity.LinkEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface LinkRepository extends BaseEntityRepository<LinkEntity, Long> {

    IPage<LinkEntity> friendLinkList(Page<Object> myBatisPlusPage,
                                     @Param("condition") Map<String, Object> condition);

}
