package cc.wdev.platform.commons.core.quartz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuartzJobInfo implements Serializable {
    private String key;
    private String className;
    private QuartzJobScheduleType scheduleType;
    private TimeUnit unit;
    private int period;
    private int hour;
    private int minute;
    private String cron;
    private String params;
}
