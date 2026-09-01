package cc.wdev.platform.system.ai.job;

import cc.wdev.platform.commons.core.quartz.QuartzJob;
import cc.wdev.platform.system.ai.api.AiKbApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 向量化任务补偿调度：
 * 1. 重试到期待重试任务（PENDING + nextRetryAt <= now）；
 * 2. 回收处理超时任务（PROCESSING 超过 maxProcessingMinutes）。
 *
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class AiKbJob extends QuartzJob {

    private final AiKbApi aiKbApi;

    @Override
    protected void execute() throws Exception {
        log.info("AiKbTaskJob start.");
        aiKbApi.execute();
        log.info("AiKbTaskJob finish.");
    }

}
