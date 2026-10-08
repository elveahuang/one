package cc.wdev.platform.commons.core.sequence.snowflake;

import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.core.sequence.Sequence;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 * <p>
 * 分布式高效有序ID生产黑科技(sequence)
 * <p>
 * 代码来自
 * <p>MyBatis-Plus：<a href="https://mybatis.plus">...</a></p>
 *
 * @author hubin
 * @since 2016-08-18
 */
@Slf4j
public class DefaultSnowflakeSequence implements Sequence {
    /**
     * 工作机器标识位数
     */
    private final static long WORKER_ID_BITS = 5L;
    /**
     * 数据中心标识位数
     */
    private final static long DATACENTER_ID_BITS = 5L;
    /**
     * 序列号位数
     */
    private final static long SEQUENCE_BITS = 12L;
    /**
     * 工作机器标识最大值
     */
    public final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /**
     * 数据中心标识最大值
     */
    public final static long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    /**
     * 序列号最大值
     */
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    /**
     * 左移位数
     */
    private final static long WORKER_ID_LEFT_SHIFT = SEQUENCE_BITS;
    private final static long DATACENTER_ID_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    /**
     * 初始时间戳
     */
    private final long epoch;
    /**
     * 工作机器ID(0-31)
     */
    private final long workerId;
    /**
     * 数据中心ID(0-31)
     */
    private final long datacenterId;
    /**
     * 毫秒内序列(0～4096)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     */
    public DefaultSnowflakeSequence() {
        this(DateTimeConstants.EPOCH, 1, 1);
    }

    /**
     * 构造函数
     *
     * @param epoch        2023-01-01 00:00:00
     * @param datacenterId 数据中心ID
     * @param workerId     工作机器ID
     */
    public DefaultSnowflakeSequence(long epoch, long datacenterId, long workerId) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException(String.format("Snowflake - datacenterId [%d] can't be greater than %d or less than 0.", datacenterId, MAX_DATACENTER_ID));
        }
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(String.format("Snowflake - workerId [%d] can't be greater than %d or less than 0.", workerId, MAX_WORKER_ID));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
        this.epoch = epoch;
    }

    /**
     * 获取下一个 ID
     *
     * @return 下一个 ID
     */
    public synchronized long nextId() {
        long timestamp = currentTimestamp();
        // 闰秒
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = currentTimestamp();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 同一毫秒的序列数已经达到最大
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为 1 - 3 随机数
            sequence = ThreadLocalRandom.current().nextLong(1, 3);
        }

        lastTimestamp = timestamp;

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return ((timestamp - this.epoch) << TIMESTAMP_LEFT_SHIFT)
            | (datacenterId << DATACENTER_ID_LEFT_SHIFT)
            | (workerId << WORKER_ID_LEFT_SHIFT)
            | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = currentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp();
        }
        return timestamp;
    }

    private long currentTimestamp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0, 0, 0);
        System.out.println(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        System.out.println(System.currentTimeMillis());

        Sequence sequence = new DefaultSnowflakeSequence(DateTimeConstants.EPOCH, 1, 1);
        for (int i = 0; i < 100; i++) {
            System.out.println(sequence.nextId());
        }
    }

}
