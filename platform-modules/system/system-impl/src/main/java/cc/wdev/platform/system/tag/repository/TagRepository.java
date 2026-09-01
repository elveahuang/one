package cc.wdev.platform.system.tag.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.tag.domain.entity.TagEntity;
import cc.wdev.platform.system.tag.domain.request.TagSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author irving
 */
@Mapper
public interface TagRepository extends BaseEntityRepository<TagEntity, Long> {

    List<TagEntity> list(@Param("request") TagSearchRequest request);
}
