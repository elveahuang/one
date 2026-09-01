package cc.wdev.platform.commons.autoconfigure.data;

import cc.wdev.platform.commons.annotations.ds.*;
import cc.wdev.platform.commons.autoconfigure.data.properties.DataSourceCustomProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static cc.wdev.platform.commons.constants.DataSourceConstants.*;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration(before = {DataSourceAutoConfiguration.class})
@ConditionalOnProperty(prefix = DataSourceCustomProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(DataSourceCustomProperties.class)
public class DataSourceCustomAutoConfiguration {

    public DataSourceCustomAutoConfiguration(DataSourceCustomProperties customDataSourceProperties) {
        log.info("DataSourceCustomAutoConfiguration is enabled");
        if (customDataSourceProperties.isMasterSlaveEnabled()) {
            log.info("DataSourceCustomAutoConfiguration master-slave is enabled");
        } else {
            log.info("DataSourceCustomAutoConfiguration master-slave is disabled");
        }
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(transactionManager);
        return transactionTemplate;
    }

    /**
     * ========================================================================================================================
     * 默认数据源配置
     * ========================================================================================================================
     */

    @Bean
    @Primary
    public DataSource dataSource(@Autowired(required = false) @Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
                                 @Autowired(required = false) @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource) {
        log.info("Creating PrimaryDataSource");
        return masterDataSource;
    }

    /**
     * ========================================================================================================================
     * 主库数据源配置
     * ========================================================================================================================
     */

    @MasterDataSourceProperties
    @Bean(name = MASTER_DATASOURCE_PROPERTIES)
    @ConfigurationProperties("spring.datasource.master")
    @ConditionalOnProperty(prefix = DataSourceCustomProperties.MASTER_DATASOURCE_PREFIX, name = "enabled", havingValue = "true")
    public DataSourceProperties masterDataSourceProperties() {
        log.info("Creating MasterDataSourceProperties");
        return new DataSourceProperties();
    }

    @MasterDataSource
    @Bean(name = MASTER_DATASOURCE)
    @ConfigurationProperties("spring.datasource.master.hikari")
    @ConditionalOnProperty(prefix = DataSourceCustomProperties.MASTER_DATASOURCE_PREFIX, name = "enabled", havingValue = "true")
    public HikariDataSource masterDataSource(@Qualifier(MASTER_DATASOURCE_PROPERTIES) DataSourceProperties properties) {
        log.info("Creating MasterDataSource");
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        initDataSource(dataSource);
        return dataSource;
    }

    /**
     * ========================================================================================================================
     * 从库数据源配置
     * ========================================================================================================================
     */

    @SlaveDataSourceProperties
    @Bean(name = SLAVE_DATASOURCE_PROPERTIES)
    @ConfigurationProperties("spring.datasource.slave")
    @ConditionalOnProperty(prefix = DataSourceCustomProperties.SLAVE_DATASOURCE_PREFIX, name = "enabled", havingValue = "true")
    public DataSourceProperties slaveDataSourceProperties() {
        log.info("Creating SlaveDataSourceProperties");
        return new DataSourceProperties();
    }

    @SlaveDataSource
    @Bean(name = SLAVE_DATASOURCE)
    @ConfigurationProperties("spring.datasource.slave.hikari")
    @ConditionalOnProperty(prefix = DataSourceCustomProperties.SLAVE_DATASOURCE_PREFIX, name = "enabled", havingValue = "true")
    public HikariDataSource slaveDataSource(@Qualifier(SLAVE_DATASOURCE_PROPERTIES) DataSourceProperties properties) {
        log.info("Creating SlaveDataSource");
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        initDataSource(dataSource);
        return dataSource;
    }

    /**
     * ========================================================================================================================
     * 任务数据源配置
     * ========================================================================================================================
     */

    @JobDataSourceProperties
    @Bean(name = JOB_DATASOURCE_PROPERTIES)
    @ConfigurationProperties("spring.datasource.job")
    @ConditionalOnProperty(prefix = DataSourceCustomProperties.JOB_DATASOURCE_PREFIX, name = "enabled", havingValue = "true")
    public DataSourceProperties jobDataSourceProperties() {
        log.info("Creating JobDataSourceProperties");
        return new DataSourceProperties();
    }

    @JobDataSource
    @Bean(name = JOB_DATASOURCE)
    @ConfigurationProperties("spring.datasource.job.hikari")
    @ConditionalOnProperty(prefix = DataSourceCustomProperties.JOB_DATASOURCE_PREFIX, name = "enabled", havingValue = "true")
    public HikariDataSource jobDataSource(@Qualifier(JOB_DATASOURCE_PROPERTIES) DataSourceProperties properties) {
        log.info("Creating JobDataSource");
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        initDataSource(dataSource);
        return dataSource;
    }

    /**
     * 初始化数据源
     */
    private void initDataSource(HikariDataSource dataSource) {
        dataSource.setMaxLifetime(60 * 60 * 1000);
        dataSource.setMinimumIdle(10);
        dataSource.setMaximumPoolSize(500);
    }

}
