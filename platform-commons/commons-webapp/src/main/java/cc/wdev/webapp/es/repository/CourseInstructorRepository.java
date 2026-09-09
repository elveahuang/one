package cc.wdev.webapp.es.repository;

import cc.wdev.platform.commons.data.elasticsearch.repository.BaseEntityRepository;
import cc.wdev.webapp.es.domain.entity.InstructorElasticEntity;
import org.springframework.stereotype.Repository;

/**
 * @author elvea
 */
@Repository
public interface CourseInstructorRepository extends BaseEntityRepository<InstructorElasticEntity, Long> {
}
