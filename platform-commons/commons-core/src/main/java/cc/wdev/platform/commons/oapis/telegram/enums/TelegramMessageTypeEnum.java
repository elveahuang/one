package cc.wdev.platform.commons.oapis.telegram.enums;

import lombok.Getter;

/**
 * @author elvea
 */
@Getter
public enum TelegramMessageTypeEnum {
    Markdown("Markdown", "Markdown"),
    Html("Html", "Html");

    private final String type;
    private final String description;

    TelegramMessageTypeEnum(final String type, final String description) {
        this.type = type;
        this.description = description;
    }

}
