package cc.wdev.platform.system.im.message;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 响应消息
 *
 * @author elvea
 */
@Data
@Slf4j
@Builder
public class ChatRespMessage<T extends Serializable> implements Serializable {
    private String type;
    private T data;
}
