package cc.wdev.platform.commons.autoconfigure.oapis.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = WeiXinCpProperties.PREFIX)
public class WeiXinCpProperties {

    public static final String PREFIX = "platform.oapis.weixin.cp";

    private boolean enabled = false;

    private String cacheKeyPrefix = "cp";

    // =================================================================================================================
    // 默认应用配置
    // =================================================================================================================

    private String corpId;

    private String corpSecret;

    private Integer agentId;

    private String token;

}
