package cc.wdev.webapp.jpa.service;

import cc.wdev.platform.commons.data.jpa.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.webapp.jpa.domain.entity.JpaUserEntity;
import cc.wdev.webapp.jpa.repository.JpaUserRepository;

public interface JpaUserService
    extends EntityService<JpaUserEntity, Long>, EnhancedEntityService<JpaUserEntity, Long, JpaUserRepository> {

    Long count(String username);

}
