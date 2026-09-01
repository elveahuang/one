package cc.wdev.platform.system.message.job;

import cc.wdev.platform.commons.core.quartz.QuartzJob;
import cc.wdev.platform.system.message.api.MessageApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MessageSendJob extends QuartzJob {

    private final MessageApi messageApi;

    @Override
    public void execute() throws Exception {
        log.info("MessageSendJob start.");
        messageApi.sendMessage();
        log.info("MessageSendJob end.");
    }
}
