package cc.wdev.platform.system.message;

import cc.wdev.platform.system.message.domain.dto.SendMessageDto;

/**
 * @author elvea
 */
public interface MessageSender {

    default void send(SendMessageDto message) {
        //
    }

}
