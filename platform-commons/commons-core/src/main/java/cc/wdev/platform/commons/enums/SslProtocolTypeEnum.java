package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum SslProtocolTypeEnum implements BaseEnum<String> {
    SSL("SSL", "SSL"),
    STARTTLS("STARTTLS", "STARTTLS");

    private final String value;
    private final String description;
}
