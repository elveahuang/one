package cc.wdev.platform.commons.message.topic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicConfig implements Serializable {

    private String name;

    public String getTopicName() {
        return name;
    }

    public String getExchange() {
        return name;
    }

    public String getDlx() {
        return name + ".dlx";
    }

    public String getRoutingKey() {
        return name;
    }

}
