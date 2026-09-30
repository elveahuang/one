package cc.wdev.webapp.es.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.webapp.es.domain.entity.CourseElasticEntity;
import org.springframework.data.domain.Page;

/**
 * @author erden
 * @see EntityService
 */
public interface CourseElasticService extends EntityService<CourseElasticEntity, Long> {

    Page<CourseElasticEntity> search(PageRequest request);

}
