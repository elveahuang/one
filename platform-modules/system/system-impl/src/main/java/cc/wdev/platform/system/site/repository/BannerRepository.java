package cc.wdev.platform.system.site.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.site.domain.entity.BannerEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface BannerRepository extends BaseEntityRepository<BannerEntity, Long> {
    IPage<BannerEntity> findForUser(Page<BannerEntity> page, @Param("condition") Map<String, Object> condition);
}
