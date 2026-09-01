package cc.wdev.webapp.es.repository;

import cc.wdev.platform.commons.data.elasticsearch.repository.BaseEntityRepository;
import cc.wdev.webapp.es.domain.entity.CourseElasticEntity;
import org.springframework.stereotype.Repository;

/**
 * @author elvea
 */
@Repository
public interface CourseElasticRepository extends BaseEntityRepository<CourseElasticEntity, Long> {
}
