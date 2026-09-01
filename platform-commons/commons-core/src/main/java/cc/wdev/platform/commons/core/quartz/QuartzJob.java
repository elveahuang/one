package cc.wdev.platform.commons.core.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * @author elvea
 */
@Slf4j
@DisallowConcurrentExecution
public abstract class QuartzJob implements Job {

    protected JobExecutionContext jobContext = null;

    /**
     * 执行定时任务
     */
    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        try {
            this.jobContext = jobContext;

            JobDetail jobDetail = jobContext.getJobDetail();

            Trigger trigger = jobContext.getTrigger();

            log.info("Job [{}] trigger [{}] exec start", jobDetail.getKey(), trigger.getKey());
            execute();
            log.info("Job [{}] trigger [{}] exec end", jobDetail.getKey(), trigger.getKey());
            log.info("Job [{}] trigger [{}] getFireTime - {}", jobDetail.getKey(), trigger.getKey(), jobContext.getFireTime());
            log.info("Job [{}] trigger [{}] getNextFireTime - {}", jobDetail.getKey(), trigger.getKey(), jobContext.getNextFireTime());
            log.info("Job [{}] trigger [{}] getPreviousFireTime - {}", jobDetail.getKey(), trigger.getKey(), jobContext.getPreviousFireTime());
            log.info("Job [{}] trigger [{}] getScheduledFireTime - {}", jobDetail.getKey(), trigger.getKey(), jobContext.getScheduledFireTime());
            log.info("Job [{}] trigger [{}] getRefireCount - {}", jobDetail.getKey(), trigger.getKey(), jobContext.getRefireCount());
        } catch (Exception e) {
            log.error("Job exec error", e);
        }
    }

    /**
     * 获取任务上下文
     */
    protected JobExecutionContext getContext() {
        return jobContext;
    }

    /**
     * 子类中实现任务的操作
     */
    protected void execute() throws Exception {
    }

}
