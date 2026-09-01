package cc.wdev.platform.system.job.enums;

import cc.wdev.platform.commons.core.quartz.QuartzJobScheduleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统默认初始的定时任务
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum JobItemTypeEnum implements BaseJobItemTypeEnum {
    SEND_MESSAGE_JOB("SEND_MESSAGE_JOB", "cc.wdev.platform.system.message.job.MessageSendJob", QuartzJobScheduleType.PERIOD.toString(), "MINUTES", 3, null, null, "发送消息定时任务"),
    SYNC_MCA_JOB("SYNC_MCA_JOB", "cc.wdev.platform.system.region.job.SyncMcaJob", QuartzJobScheduleType.DAILY.toString(), null, null, 1, 0, "区划同步定时任务"),
    AI_KB_JOB("AI_KB_JOB", "cc.wdev.platform.system.ai.job.AiKbJob", QuartzJobScheduleType.DAILY.toString(), null, null, 1, 0, "知识库向量化任务补偿"),
    ;

    private final String code;
    private final String className;
    private final String type;
    private final String unit;
    private final Integer period;
    private final Integer hour;
    private final Integer minute;
    private final String description;

}
