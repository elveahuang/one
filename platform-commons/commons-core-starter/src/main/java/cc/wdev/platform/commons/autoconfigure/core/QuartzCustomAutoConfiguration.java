package cc.wdev.platform.commons.autoconfigure.core;

import cc.wdev.platform.commons.annotations.ds.JobDataSource;
import cc.wdev.platform.commons.annotations.ds.JobTransactionManager;
import cc.wdev.platform.commons.core.quartz.QuartzJobManager;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.quartz.autoconfigure.QuartzProperties;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration(after = {DataSourceAutoConfiguration.class})
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class, PlatformTransactionManager.class})
@EnableConfigurationProperties({QuartzProperties.class})
public class QuartzCustomAutoConfiguration {

    private final QuartzProperties quartzProperties;

    public QuartzCustomAutoConfiguration(QuartzProperties quartzProperties) {
        this.quartzProperties = quartzProperties;
        log.info("QuartzCustomAutoConfiguration is enabled");
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(DataSource dataSource,
                                                                         @JobDataSource ObjectProvider<DataSource> jobDataSource,
                                                                         PlatformTransactionManager transactionManager,
                                                                         @JobTransactionManager ObjectProvider<PlatformTransactionManager> jobTransactionManager) {
        return (schedulerFactoryBean) -> {
            log.info("Quartz DataSource is enabled");
            schedulerFactoryBean.setSchedulerName(nvl(this.quartzProperties.getSchedulerName(), "QuartzScheduler"));
            schedulerFactoryBean.setDataSource(jobDataSource.getIfAvailable(() -> dataSource));
            schedulerFactoryBean.setTransactionManager(jobTransactionManager.getIfAvailable(() -> transactionManager));
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public QuartzJobManager quartzJobManager(@Autowired(required = false) SchedulerFactoryBean schedulerFactoryBean) {
        return new QuartzJobManager(schedulerFactoryBean.getScheduler());
    }

}
