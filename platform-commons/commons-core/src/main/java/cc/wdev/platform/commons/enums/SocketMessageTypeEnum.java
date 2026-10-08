package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum SocketMessageTypeEnum implements BaseEnum<String> {
    PING("ping", "PING"),
    VALID("valid", "校验"),
    ACK("ack", "确认"),
    CHAT("chat", "沟通");

    private final String value;
    private final String description;
}
