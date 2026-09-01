package cc.wdev.platform.system.im.utils;

import cc.wdev.platform.commons.message.model.SimpleJsonMessage;
import cc.wdev.platform.commons.message.model.SimpleTextMessage;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.im.message.ChatRespMessage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author elvea
 */
public abstract class ImUtils {

    public static <T extends Serializable> ChatRespMessage<T> createChatRespMessage(String type, T data) {
        return ChatRespMessage.<T>builder()
            .type(type)
            .data(data)
            .build();
    }

    public static <T extends Serializable> SimpleJsonMessage<T> createJsonMessage(Collection<Long> receivers, List<String> keys, T data) {
        SimpleJsonMessage<T> message = new SimpleJsonMessage<>();
        if (CollectionUtils.isNotEmpty(receivers)) {
            message.setReceivers(receivers);
        }
        if (CollectionUtils.isNotEmpty(keys)) {
            message.setKeys(keys);
        }
        message.setContent(data);
        return message;
    }

    public static <T extends Serializable> SimpleJsonMessage<T> createJsonMessage(Collection<Long> receivers, T data) {
        return createJsonMessage(receivers, emptyList(), data);
    }

    public static <T extends Serializable> SimpleJsonMessage<T> createJsonMessage(T data) {
        return createJsonMessage(emptyList(), emptyList(), data);
    }

    public static SimpleTextMessage createTextMessage(List<Long> receivers, List<String> keys, String text) {
        SimpleTextMessage message = new SimpleTextMessage();
        if (CollectionUtils.isNotEmpty(receivers)) {
            message.setReceivers(receivers);
        }
        if (CollectionUtils.isNotEmpty(keys)) {
            message.setKeys(keys);
        }
        message.setContent(text);
        return message;
    }

    public static SimpleTextMessage createTextMessage(List<Long> receivers, String text) {
        return createTextMessage(receivers, emptyList(), text);
    }

    public static SimpleTextMessage createTextMessage(String text) {
        return createTextMessage(emptyList(), emptyList(), text);
    }

}
