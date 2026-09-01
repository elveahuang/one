package cc.wdev.platform.commons.data.jdbc.dao;

import cc.wdev.platform.commons.data.jdbc.dialect.DbDialect;
import cc.wdev.platform.commons.data.jdbc.utils.JdbcUtils;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author elvea
 */
public class Dao {

    private final JdbcTemplate jdbcTemplate;

    private final DbDialect dialect;

    public Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = this.jdbcTemplate.execute(JdbcUtils::getDialect);
    }

    /**
     * 获取数据库系统当前时间
     *
     * @return 数据库系统当前时间
     */
    public LocalDateTime getCurrentLocalDateTime() {
        return this.jdbcTemplate.queryForObject(this.dialect.dateTime().currentDateTime(), LocalDateTime.class);
    }

    /**
     * 获取数据库系统当前时间
     *
     * @return 数据库系统当前时间
     */
    public LocalDate getCurrentLocalDate() {
        return this.jdbcTemplate.queryForObject(this.dialect.dateTime().currentDate(), LocalDate.class);
    }

    /**
     * 获取数据库系统当前时间
     *
     * @return 数据库系统当前时间
     */
    public LocalTime getCurrentLocalTime() {
        return this.jdbcTemplate.queryForObject(this.dialect.dateTime().currentTime(), LocalTime.class);
    }

    /**
     * 创建简单临时表
     */
    public String createTemporaryTable(List<Long> data) {
        return this.jdbcTemplate.execute((ConnectionCallback<String>) connection -> JdbcUtils.createTemporaryTable(connection, data));
    }

    /**
     * 创建简单临时表
     */
    public String createTemporaryTable(List<Long> data, int batchSize) {
        return this.jdbcTemplate.execute((ConnectionCallback<String>) connection -> JdbcUtils.createTemporaryTable(connection, data, batchSize));
    }

    /**
     * 删除临时表
     */
    public void dropTemporaryTable(String temporaryTableName) {
        this.jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
            JdbcUtils.dropTemporaryTable(connection, temporaryTableName);
            return Boolean.TRUE;
        });
    }

}
