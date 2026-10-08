package cc.wdev.platform.system.job.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.job.domain.entity.JobEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Belly
 */
@Repository
@Mapper
public interface JobRepository extends BaseEntityRepository<JobEntity, Long> {
}
