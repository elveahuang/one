package cc.wdev.platform.system.job.api;

import cc.wdev.platform.commons.core.quartz.QuartzJobManager;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.job.domain.entity.JobEntity;
import cc.wdev.platform.system.job.enums.BaseJobItemTypeEnum;
import cc.wdev.platform.system.job.service.JobService;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;

@Slf4j
@Service
@AllArgsConstructor
public class JobApiImpl implements JobApi {

    private QuartzJobManager quartzJobManager;

    private JobService jobService;

    @Override
    public void initialize() {
        log.info("Initialize job start.");

        // 扫描枚举
        List<BaseJobItemTypeEnum> enumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseJobItemTypeEnum.class);
        if (CollectionUtils.isEmpty(enumList)) {
            log.info("Initialize job skip. no dict item type enum.");
            return;
        }

        // 待处理字典实体
        List<JobEntity> updateEntityList = Lists.newArrayList();
        List<JobEntity> insertEntityList = Lists.newArrayList();

        for (BaseJobItemTypeEnum itemEnum : enumList) {
            JobEntity entity = this.jobService.findByCode(itemEnum.getCode());
            if (entity != null) {
                updateEntityList.add(entity);
            } else {
                entity = new JobEntity();
                insertEntityList.add(entity);
            }
            entity.setCode(itemEnum.getCode());
            entity.setClassname(itemEnum.getClassName());
            entity.setType(itemEnum.getType());
            entity.setUnit(itemEnum.getUnit());
            entity.setPeriod(itemEnum.getPeriod());
            entity.setHour(itemEnum.getHour());
            entity.setMinute(itemEnum.getMinute());
            entity.setStatus(StatusTypeEnum.ON.getValue());
            entity.setActive(BooleanTypeEnum.TRUE.getValue());
        }

        this.jobService.insertBatch(insertEntityList);
        this.jobService.updateBatchById(updateEntityList);

        log.info("Initialize dict done.");

        // 启动定时任务
        List<JobEntity> jobEntities = jobService.jobEntityListByStatus();
        jobEntities.forEach(jobEntity ->
            quartzJobManager.schedule(jobService.getQuartzJobInfo(jobEntity), DateUtil.date())
        );
    }

}
