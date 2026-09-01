package cc.wdev.platform.commons.oapis.dingtalk.config;

import com.aliyun.teaopenapi.models.Config;
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
    private String protocol = "https";

    @Builder.Default
    private String regionId = "central";

    private String corpId;

    private String agentId;

    private String appKey;

    private String appSecret;

    public Config getConfig() {
        // 默认配置信息
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return config;
    }

}
