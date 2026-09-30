package cc.wdev.platform.commons.message.topic;

import cc.wdev.platform.commons.message.model.SimpleMessage;

/**
 * @author elvea
 */
public interface TopicService<T extends SimpleMessage<?>> {

    /**
     * 发布消息
     */
    void publish(T message) throws Exception;

    /**
     * 处理消息
     */
    void handle(T message) throws Exception;

}
