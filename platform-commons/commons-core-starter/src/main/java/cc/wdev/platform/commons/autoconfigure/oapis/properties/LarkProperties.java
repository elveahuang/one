package cc.wdev.platform.commons.autoconfigure.oapis.properties;

import com.lark.oapi.core.enums.AppType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = LarkProperties.PREFIX)
public class LarkProperties {

    public static final String PREFIX = "platform.oapis.lark";

    private boolean enabled = false;

    private String cacheKeyPrefix = "lark";

    // =================================================================================================================
    // 默认应用配置
    // =================================================================================================================

    private AppType appType = AppType.SELF_BUILT;

    private String appId;

    private String appSecret;

    private String verificationToken;

    private String encryptKey;

    private boolean debug = false;

}
