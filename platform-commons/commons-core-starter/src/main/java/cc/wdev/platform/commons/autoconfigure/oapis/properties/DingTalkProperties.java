package cc.wdev.platform.commons.autoconfigure.oapis.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = DingTalkProperties.PREFIX)
public class DingTalkProperties {

    public static final String PREFIX = "platform.oapis.dingtalk";

    private boolean enabled = false;

    private String cacheKeyPrefix = "dingtalk";

    // =================================================================================================================
    // 默认应用配置
    // =================================================================================================================

    private String corpId;

    private String agentId;

    private String appKey;

    private String appSecret;

}
