package cc.wdev.platform.commons.message.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author elvea
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SimpleTextMessage extends SimpleMessage<String> {
    public SimpleTextMessage() {
        setType(TEXT);
    }
}
