package cc.wdev.platform.commons.data.jpa.repository;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * @param <T> 实体
 * @param <K> 主键
 * @author elvea
 * @see Repository
 * @see JpaRepository
 * @see CrudRepository
 */
@NoRepositoryBean
public interface BaseEntityRepository<T, K> extends JpaRepository<T, K>, JpaSpecificationExecutor<T> {

    /**
     * EntityManager
     */
    EntityManager getEntityManager();

}
