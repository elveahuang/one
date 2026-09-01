package cc.wdev.platform.system.job.task;

import cc.wdev.platform.commons.core.quartz.QuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;

/**
 * 系统预警定时任务
 *
 * @author Belly
 */
@Slf4j
public class TestJob extends QuartzJob {

    @Override
    protected void execute() {
        JobDataMap map = getContext().getMergedJobDataMap();
        log.info("JobDataMap: {}", map);
        log.info("s: {}", map.getString("s"));
        log.info("q: {}", map.getString("q"));
        log.info("This is a test job");
    }

}
