package cc.wdev.platform.system.core.socket;

import cc.wdev.platform.commons.message.socket.servlet.ServletWebSocketManager;
import cc.wdev.platform.commons.message.socket.servlet.ServletWebSocketService;
import cc.wdev.platform.commons.message.topic.TopicConfig;
import cc.wdev.platform.commons.message.topic.TopicManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public class SystemWebSocketService extends ServletWebSocketService {

    public SystemWebSocketService(TopicManager topicManager, ServletWebSocketManager servletWebSocketManager) {
        log.info("Create SystemWebSocketService.");
        super(TopicConfig.builder().name("system_socket").build(), topicManager, servletWebSocketManager);
    }

}
