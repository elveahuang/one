package cc.wdev.platform.commons.oapis.dingtalk.bean;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class JsapiSignature implements Serializable {

    @Schema(description = "应用ID")
    private String agentId;

    @Schema(description = "企业ID")
    private String corpId;

    @Schema(description = "随机字符串")
    private String nonceStr;

    @Schema(description = "时间戳")
    private long timestamp;

    @Schema(description = "请求URL")
    private String url;

    @Schema(description = "签名")
    private String signature;

}
