package cc.wdev.webapp.jpa.repository;

import cc.wdev.platform.commons.data.jpa.repository.BaseEntityRepository;
import cc.wdev.webapp.jpa.domain.entity.JpaUserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends BaseEntityRepository<JpaUserEntity, Long> {
}
