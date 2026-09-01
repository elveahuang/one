package cc.wdev.platform.commons.oapis.lark.bean;

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
@Schema(description = "飞书鉴权信息")
public class JsapiSignature implements Serializable {

    @Schema(description = "飞书应用编号")
    private String appId;

    @Schema(description = "随机字符串")
    private String nonceStr;

    @Schema(description = "当前时间戳")
    private long timestamp;

    @Schema(description = "当前网页地址")
    private String url;

    @Schema(description = "签名")
    private String signature;

}
