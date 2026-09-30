package cc.wdev.platform.commons.autoconfigure.oapis.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = WeiXinMaProperties.PREFIX)
public class WeiXinMaProperties {

    public static final String PREFIX = "platform.oapis.weixin.ma";

    private boolean enabled = false;

    private String cacheKeyPrefix = "ma";

    // =================================================================================================================
    // 默认应用配置
    // =================================================================================================================

    private String appId;

    private String appSecret;

}
