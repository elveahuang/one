package cc.wdev.platform.system.site.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.site.domain.entity.KeywordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author elvea
 */
@Repository
@Mapper
public interface KeywordRepository extends BaseEntityRepository<KeywordEntity, Long> {
}
