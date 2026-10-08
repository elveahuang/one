package cc.wdev.platform.commons.message.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

import static cc.wdev.platform.commons.message.model.SimpleMessage.JSON;
import static cc.wdev.platform.commons.message.model.SimpleMessage.TEXT;

/**
 * @author elvea
 */
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "contentType",
    defaultImpl = SimpleTextMessage.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SimpleTextMessage.class, name = TEXT),
    @JsonSubTypes.Type(value = SimpleJsonMessage.class, name = JSON)
})
public class SimpleMessage<M extends Serializable> implements Serializable {

    public static final String TEXT = "text";

    public static final String JSON = "json";

    /**
     * 指定会话标识
     */
    private Collection<String> keys;
    /**
     * 指定接收者
     */
    private Collection<Long> receivers;
    /**
     * 消息类型
     */
    private String type;
    /**
     * 消息内容
     */
    private M content;
}
