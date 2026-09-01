package cc.wdev.platform.commons.message.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class SimpleJsonMessage<C extends Serializable> extends SimpleMessage<C> {
    public SimpleJsonMessage() {
        setType(JSON);
    }
}
