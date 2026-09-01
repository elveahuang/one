package cc.wdev.platform.commons.oapis.weixin.config;

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
public class AppMpConfig implements Serializable {

    private String appId;

    private String appSecret;

    private String aesKey;

    private String token;
}
