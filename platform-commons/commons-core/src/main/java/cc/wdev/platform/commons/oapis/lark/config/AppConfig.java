package cc.wdev.platform.commons.oapis.lark.config;

import cc.wdev.platform.commons.oapis.lark.cache.Cache;
import cc.wdev.platform.commons.oapis.lark.cache.LocalCache;
import com.lark.oapi.core.Config;
import com.lark.oapi.core.enums.AppType;
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
public class AppConfig implements Serializable {

    @Builder.Default
    private Cache cache = LocalCache.getInstance();

    @Builder.Default
    private AppType appType = AppType.SELF_BUILT;

    private String appId;

    private String appSecret;

    private String verificationToken;

    private String encryptKey;

    @Builder.Default
    private Boolean debug = Boolean.FALSE;

    public Config getConfig() {
        Config config = new Config();
        config.setAppType(this.appType);
        config.setAppId(this.appId);
        config.setAppSecret(this.appSecret);
        config.setCache(this.cache);
        return config;
    }

}
