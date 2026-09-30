package cc.wdev.webapp.jpa.service.impl;

import cc.wdev.platform.commons.data.jpa.service.BaseEntityService;
import cc.wdev.webapp.jpa.domain.entity.JpaUserEntity;
import cc.wdev.webapp.jpa.repository.JpaUserRepository;
import cc.wdev.webapp.jpa.service.JpaUserService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.core.PropertyPath;
import org.springframework.data.core.PropertyReference;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class JpaUserServiceImpl extends BaseEntityService<JpaUserEntity, Long, JpaUserRepository> implements JpaUserService {

    /**
     * @see JpaUserService#count(String)
     */
    public Long count(String username) {
        PropertyReference.of(JpaUserEntity::getUsername);
        Specification<JpaUserEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(builder.equal(root.get(PropertyPath.of(JpaUserEntity::getUsername).getSegment()), username));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        return this.repository.count(specification);
    }

}
