package cc.wdev.platform.commons.constants;

/**
 * @author elvea
 */
public interface DataSourceConstants {
    /**
     * 主数据源
     */
    String MASTER_DATASOURCE = "masterDataSourceConfig";
    String MASTER_DATASOURCE_PROPERTIES = "masterDataSourceProperties";
    /**
     * 从数据源
     */
    String SLAVE_DATASOURCE = "slaveDataSourceConfig";
    String SLAVE_DATASOURCE_PROPERTIES = "slaveDataSourceProperties";
    /**
     * 定时任务数据源
     */
    String JOB_DATASOURCE = "jobDataSourceConfig";
    String JOB_DATASOURCE_PROPERTIES = "jobDataSourceProperties";
}
