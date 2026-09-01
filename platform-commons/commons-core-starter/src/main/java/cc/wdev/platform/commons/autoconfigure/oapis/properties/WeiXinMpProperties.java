package cc.wdev.platform.commons.autoconfigure.oapis.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = WeiXinMpProperties.PREFIX)
public class WeiXinMpProperties {

    public static final String PREFIX = "platform.oapis.weixin.mp";

    private boolean enabled = false;

    private String cacheKeyPrefix = "mp";

    // =================================================================================================================
    // 默认应用配置
    // =================================================================================================================

    private String appId;

    private String appSecret;

    private String aesKey;

    private String token;

}
