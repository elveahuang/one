package cc.wdev.platform.commons.data.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

/**
 * @param <T> 实体
 * @param <K> 主键
 * @author elvea
 * @see ElasticsearchRepository
 * @see PagingAndSortingRepository
 * @see CrudRepository
 * @see Repository
 */
@NoRepositoryBean
public interface BaseEntityRepository<T, K> extends ElasticsearchRepository<T, K> {
}
