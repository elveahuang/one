package cc.wdev.platform.system.job.service;

import cc.wdev.platform.commons.core.quartz.QuartzJobInfo;
import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.job.domain.entity.JobEntity;
import cc.wdev.platform.system.job.domain.request.JobAddRequest;
import cc.wdev.platform.system.job.domain.request.JobSaveRequest;
import cc.wdev.platform.system.job.domain.request.JobSearchRequest;
import cc.wdev.platform.system.job.domain.vo.JobVO;
import cc.wdev.platform.system.job.repository.JobRepository;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Belly
 */
public interface JobService extends CachingEntityService<JobEntity, Long>, EnhancedEntityService<JobEntity, Long, JobRepository> {

    R<?> add(JobAddRequest jobAddRequest);

    R<?> delete(JobSearchRequest jobSearchRequest);

    R<?> save(JobSaveRequest jobSaveRequest);

    R<JobVO> detail(Long id);

    /**
     * 任务列表
     */
    Page<JobVO> jobList(JobSearchRequest jobSearchRequest);

    /**
     * 校验cron表达式是否合法
     */
    R<?> cronCheck(String cron);

    /**
     * 执行一次任务
     */
    R<?> runOnce(Long id);

    /**
     * 切换任务状态
     */
    R<?> toggleStatus(Long id);

    /**
     * 任务实体转QuartzJobInfo实体
     */
    QuartzJobInfo getQuartzJobInfo(JobEntity jobEntity);

    /**
     * 获取已启用的任务列表
     */
    List<JobEntity> jobEntityListByStatus();
}
