package cc.wdev.platform.system.job.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.core.quartz.QuartzJobInfo;
import cc.wdev.platform.commons.core.quartz.QuartzJobManager;
import cc.wdev.platform.commons.core.quartz.QuartzJobScheduleType;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.DateUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.job.domain.converter.JobConverter;
import cc.wdev.platform.system.job.domain.entity.JobEntity;
import cc.wdev.platform.system.job.domain.request.JobAddRequest;
import cc.wdev.platform.system.job.domain.request.JobSaveRequest;
import cc.wdev.platform.system.job.domain.request.JobSearchRequest;
import cc.wdev.platform.system.job.domain.vo.JobVO;
import cc.wdev.platform.system.job.repository.JobRepository;
import cc.wdev.platform.system.job.service.JobService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.CronExpression;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.JOB;

/**
 * @author Belly
 */
@Slf4j
@Service
@AllArgsConstructor
public class JobServiceImpl extends BaseCachingEntityService<JobEntity, Long, JobRepository> implements JobService {

    private QuartzJobManager quartzJobManager;

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleCacheKeyGenerator(JOB);

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return this.cacheKeyGenerator;
    }

    @Override
    public R<?> add(JobAddRequest jobAddRequest) {
        if (null == jobAddRequest) {
            return R.error();
        }
        JobEntity jobEntity = JobConverter.INSTANCE.JobAddRequestToJobEntity(jobAddRequest);
        if (StatusTypeEnum.ON.getValue().equals(jobEntity.getStatus())) {
            QuartzJobInfo quartzJobInfo = getQuartzJobInfo(jobEntity);
            // 立即启动
            quartzJobManager.schedule(quartzJobInfo, DateUtils.date());
        }
        this.save(jobEntity);
        return R.success();
    }

    @Override
    public R<?> delete(JobSearchRequest jobSearchRequest) {
        if (null != jobSearchRequest && null != jobSearchRequest.getIds() && jobSearchRequest.getIds().length > 0) {
            List<JobEntity> jobEntities = this.findByIds(Arrays.asList(jobSearchRequest.getIds()));
            jobEntities.forEach(jobEntity -> {
                // 判断任务是否启用
                if (StatusTypeEnum.ON.getValue().equals(jobEntity.getStatus())) {
                    QuartzJobInfo quartzJobInfo = getQuartzJobInfo(jobEntity);
                    // 停止任务
                    quartzJobManager.pause(quartzJobInfo);
                    // 删除任务
                    quartzJobManager.delete(quartzJobInfo);
                }
            });
            this.softDeleteBatchById(Arrays.asList(jobSearchRequest.getIds()));
            return R.success();
        }
        return R.error();
    }

    @Override
    public R<?> save(JobSaveRequest jobSaveRequest) {
        JobEntity jobEntity = JobConverter.INSTANCE.JobEditRequestToJobEntity(jobSaveRequest);
        QuartzJobInfo quartzJobInfo = getQuartzJobInfo(jobEntity);
        if (quartzJobInfo.getParams() != null && !quartzJobInfo.getParams().isEmpty() && !JSONUtil.isTypeJSON(quartzJobInfo.getParams())) {
            throw new RuntimeException("任务参数格式错误");
        }
        if (jobSaveRequest.getId() > 0) {
            // 编辑
            // 判断任务是否为禁用状态
            if (StatusTypeEnum.OFF.getValue().equals(jobEntity.getStatus())) {
                // 停止任务
                quartzJobManager.pause(quartzJobInfo);
                // 删除任务
                quartzJobManager.delete(quartzJobInfo);
            } else {
                // 立即启动
                quartzJobManager.schedule(quartzJobInfo, DateUtils.date());
            }
            jobEntity.setId(jobSaveRequest.getId());
            this.updateById(jobEntity);
        } else {
            // 新增
            if (StatusTypeEnum.ON.getValue().equals(jobEntity.getStatus())) {
                // 立即启动
                quartzJobManager.schedule(quartzJobInfo, DateUtils.date());
            }
            this.save(jobEntity);
        }
        return R.success();
    }

    @Override
    public R<JobVO> detail(Long id) {
        if (null == id) {
            return R.error();
        }
        JobEntity jobEntity = this.findById(id);
        JobVO jobVO = JobConverter.INSTANCE.JobEntityToJobVO(jobEntity);
        return R.success(jobVO);
    }

    @Override
    public Page<JobVO> jobList(JobSearchRequest jobSearchRequest) {
        IPage<JobEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(jobSearchRequest.getQ()), wrapper -> wrapper
                .like(JobEntity::getCode, jobSearchRequest.getQ()))
            .eq(JobEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(jobSearchRequest.getPageable()));
        IPage<JobVO> result = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setCurrent(page.getCurrent());
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            result.setRecords(page.getRecords()
                .stream()
                .map(JobConverter.INSTANCE::JobEntityToJobVO)
                .collect(Collectors.toList()));
        }
        return MyBatisPlusUtils.toSpringDataPage(result);
    }

    @Override
    public R<?> cronCheck(String cron) {
        if (StringUtils.isEmpty(cron)) {
            return R.error();
        }
        boolean validExpression = CronExpression.isValidExpression(cron);
        if (!validExpression) {
            return R.error();
        }
        return R.success();
    }

    /**
     * @see JobService#runOnce(Long)
     */
    @Override
    public R<?> runOnce(Long id) {
        if (null == id) {
            return R.error();
        }
        JobEntity jobEntity = this.findCacheById(id);
        if (jobEntity != null
            && ActiveTypeEnum.ENABLED.getValue().equals(jobEntity.getActive())
            && StatusTypeEnum.ON.getValue().equals(jobEntity.getStatus())) {
            QuartzJobInfo quartzJobInfo = getQuartzJobInfo(jobEntity);
            // 运行一次任务
            quartzJobManager.run(quartzJobInfo);
        } else {
            return R.error();
        }
        return R.success();
    }

    @Override
    public R<?> toggleStatus(Long id) {
        if (null == id) {
            return R.error();
        }
        JobEntity jobEntity = this.findCacheById(id);
        QuartzJobInfo quartzJobInfo = getQuartzJobInfo(jobEntity);
        // 判断任务是否启用
        if (StatusTypeEnum.ON.getValue().equals(jobEntity.getStatus())) {
            jobEntity.setStatus(StatusTypeEnum.OFF.getValue());
            // 停止任务
            quartzJobManager.pause(quartzJobInfo);
            // 删除任务
            quartzJobManager.delete(quartzJobInfo);
        } else {
            jobEntity.setStatus(StatusTypeEnum.ON.getValue());
            // 立即启动
            quartzJobManager.schedule(quartzJobInfo, DateUtils.date());
        }
        this.save(jobEntity);
        this.deleteCache(jobEntity);
        return R.success();
    }

    /**
     * 获取QuartzJobInfo
     */
    @NotNull
    public QuartzJobInfo getQuartzJobInfo(JobEntity jobEntity) {
        QuartzJobInfo quartzJobInfo = JobConverter.INSTANCE.JobEntityToQuartzJobInfo(jobEntity);
        quartzJobInfo.setClassName(jobEntity.getClassname());
        quartzJobInfo.setKey(jobEntity.getCode());
        quartzJobInfo.setParams(jobEntity.getParams());
        if ("CRON".equals(jobEntity.getType())) {
            quartzJobInfo.setScheduleType(QuartzJobScheduleType.CRON);
        }
        if ("DAILY".equals(jobEntity.getType())) {
            quartzJobInfo.setScheduleType(QuartzJobScheduleType.DAILY);
        }
        if ("PERIOD".equals(jobEntity.getType())) {
            if ("HOURS".equals(jobEntity.getUnit())) {
                quartzJobInfo.setUnit(TimeUnit.HOURS);
            } else if ("MINUTES".equals(jobEntity.getUnit())) {
                quartzJobInfo.setUnit(TimeUnit.MINUTES);
            } else if ("SECONDS".equals(jobEntity.getUnit())) {
                quartzJobInfo.setUnit(TimeUnit.SECONDS);
            }
            quartzJobInfo.setScheduleType(QuartzJobScheduleType.PERIOD);
        }
        return quartzJobInfo;
    }

    public List<JobEntity> jobEntityListByStatus() {
        return this.lambdaQueryWrapper()
            .eq(JobEntity::getStatus, StatusTypeEnum.ON.getValue())
            .eq(JobEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    @Override
    public void deleteCache(JobEntity model) {
        if (model == null || model.getId() == null) {
            return;
        }
        getCacheService().delete(this.cacheKeyGenerator.byId(model.getId()));
    }

    @Override
    public void setCache(JobEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(this.cacheKeyGenerator.byId(model.getId()), model);
            }
        }
    }
}
