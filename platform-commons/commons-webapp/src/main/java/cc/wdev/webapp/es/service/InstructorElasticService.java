package cc.wdev.webapp.es.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.webapp.es.domain.entity.InstructorElasticEntity;
import org.springframework.data.domain.Page;

/**
 * @author erden
 * @see EntityService
 */
public interface InstructorElasticService extends EntityService<InstructorElasticEntity, Long> {

    Page<InstructorElasticEntity> search(PageRequest request);

}
