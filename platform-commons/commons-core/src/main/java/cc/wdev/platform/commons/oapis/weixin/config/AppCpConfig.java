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
public class AppCpConfig implements Serializable {

    private String corpId;

    private String corpSecret;

    private Integer agentId;

    private String token;

}
