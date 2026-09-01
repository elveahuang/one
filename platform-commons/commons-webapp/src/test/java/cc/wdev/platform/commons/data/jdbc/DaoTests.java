package cc.wdev.platform.commons.data.jdbc;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.core.sequence.Sequence;
import cc.wdev.platform.commons.data.jdbc.dao.Dao;
import cc.wdev.platform.commons.utils.StopWatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author elvea
 */
@Slf4j
public class DaoTests extends BaseTests {

    @Autowired
    Sequence sequence;

    @Autowired
    Dao dao;

    @Test
    public void base() {
        Assertions.assertNotNull(this.dao);
        LocalDate localDate = this.dao.getCurrentLocalDate();
        Assertions.assertNotNull(localDate);
        LocalDateTime localDateTime = this.dao.getCurrentLocalDateTime();
        Assertions.assertNotNull(localDateTime);
        LocalTime localTime = this.dao.getCurrentLocalTime();
        Assertions.assertNotNull(localTime);
    }

    @Test
    public void createTemporaryTableTest() {
        String tableName = null;
        try {
            List<Long> idList = Lists.newArrayList();

            AtomicLong index = new AtomicLong(1);
            while (true) {
                long id = index.getAndIncrement();
                idList.add(id);
                if (id >= 10000) {
                    break;
                }
            }

            StopWatch stopWatch = StopWatchUtils.createStopWatch("临时表");

            stopWatch.start("创建临时表并插入临时数据 - 1");
            tableName = this.dao.createTemporaryTable(idList);
            stopWatch.stop();

            stopWatch.start("创建临时表并插入临时数据 - 2");
            tableName = this.dao.createTemporaryTable(idList, 100);
            stopWatch.stop();

            log.info(stopWatch.prettyPrint());
        } finally {
            this.dao.dropTemporaryTable(tableName);
        }
        Assertions.assertNotNull(tableName);
    }

}
