package cc.wdev.platform.commons.data.jpa.repository;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

/**
 * @param <T> 实体
 * @param <K> 主键
 * @author elvea
 * @see BaseEntityRepository
 * @see JpaRepository
 * @see PagingAndSortingRepository
 * @see CrudRepository
 * @see Repository
 */
@NoRepositoryBean
public class BaseEntityRepositoryImpl<T extends IdEntity, K extends Serializable> extends SimpleJpaRepository<T, K>
    implements BaseEntityRepository<T, K> {

    private final EntityManager entityManager;

    public BaseEntityRepositoryImpl(JpaEntityInformation<T, K> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityManager = entityManager;
    }

    public BaseEntityRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager), entityManager);

        this.entityManager = entityManager;
    }

    /**
     * @see BaseEntityRepository#getEntityManager()
     */
    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

}
