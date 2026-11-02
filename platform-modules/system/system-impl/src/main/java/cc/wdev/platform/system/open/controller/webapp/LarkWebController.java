package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.oapis.lark.bean.JsapiSignature;
import cc.wdev.platform.commons.oapis.lark.service.LarkService;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.util.CallbackCryptoUtils;
import cc.wdev.platform.system.open.api.LarkApi;
import cc.wdev.platform.system.open.domain.vo.LarkAppVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * 飞书控制器。
 *
 * <p>事件订阅协议：请求头带 {@code X-Lark-Request-Timestamp} / {@code X-Lark-Request-Nonce} / {@code X-Lark-Signature}，
 * 签名 = SHA256(timestamp + nonce + EncryptKey + body) 的十六进制串；加密模式下 body 为 {@code {"encrypt": "..."}}，
 * 用 EncryptKey 做 AES-256-CBC 解密。</p>
 *
 * <p>URL 校验（{@code challenge}）不需要验签，直接回显即可。</p>
 *
 * @author elvea
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "LarkController", description = "飞书控制器")
public class LarkWebController extends AbstractController {

    private final ObjectProvider<LarkService> larkServiceProvider;

    private final LarkApi larkApi;

    @Value("${platform.oapis.lark.encrypt-key:}")
    private String encryptKey;

    @PermitAll
    @Operation(summary = "获取飞书配置")
    @ApiResponse(description = "获取飞书配置")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/lark/config")
    public R<LarkAppVo> getConfig() {
        return R.success(this.larkApi.getLarkApp());
    }

    @PermitAll
    @Operation(summary = "获取飞书签名")
    @ApiResponse(description = "获取飞书签名")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/lark/signature")
    public R<JsapiSignature> getSignature(@Parameter(description = "请求URL") @RequestParam("url") String url) throws Exception {
        LarkService service = this.larkServiceProvider.getIfAvailable();
        if (service == null) {
            throw new ServiceException(ResponseCodeEnum.OAPI__NOT_IMPLEMENTED);
        }
        log.info("get lark signature for url [{}]", url);
        return R.success(service.createJsapiSignature(url));
    }

    /**
     * 飞书事件回调。
     *
     * <p>回调由飞书服务器发起，无法携带本系统令牌，安全性由签名校验 + AES 解密保证。</p>
     */
    @PermitAll
    @Operation(summary = "飞书事件回调")
    @ApiResponse(description = "飞书事件回调")
    @PostMapping(value = API_V1_PREFIX + "/oapis/lark/callback", produces = "application/json")
    public Map<String, Object> callback(
        @RequestHeader(value = "X-Lark-Request-Timestamp", required = false) String timestamp,
        @RequestHeader(value = "X-Lark-Request-Nonce", required = false) String nonce,
        @RequestHeader(value = "X-Lark-Signature", required = false) String signature,
        @RequestBody(required = false) String body) {
        if (StringUtils.isEmpty(body)) {
            return Map.of("code", 0);
        }
        // URL 校验：飞书发来 {"challenge": "xxx"}，直接回显，不需验签
        Map<String, Object> payload = JacksonUtils.toMap(body);
        if (payload != null && payload.get("encrypt") == null && payload.get("challenge") != null) {
            return Map.of("challenge", String.valueOf(payload.get("challenge")));
        }
        // 配置了 EncryptKey 则强制验签 + 解密
        if (StringUtils.isNotEmpty(this.encryptKey)) {
            String expected = CallbackCryptoUtils.larkSignature(timestamp, nonce, this.encryptKey, body);
            if (!CallbackCryptoUtils.safeEquals(expected, signature)) {
                log.warn("Lark callback signature mismatch. timestamp - [{}].", timestamp);
                return Map.of("code", 1, "msg", "invalid signature");
            }
        }
        try {
            String plain = this.resolvePlainBody(body, payload);
            log.info("Lark callback received. content - [{}].", plain);
            this.dispatch(plain);
        } catch (Exception e) {
            log.error("Lark callback decrypt failed.", e);
            return Map.of("code", 1, "msg", "decrypt failed");
        }
        return Map.of("code", 0);
    }

    /**
     * 明文分发扩展点
     */
    protected void dispatch(String plainBody) {
        // 预留扩展：接入飞书事件时按 event.type 分发
    }

    private String resolvePlainBody(String body, Map<String, Object> payload) {
        if (StringUtils.isEmpty(this.encryptKey) || payload == null) {
            return body;
        }
        Object encrypt = payload.get("encrypt");
        if (encrypt == null) {
            return body;
        }
        return CallbackCryptoUtils.decrypt(this.encryptKey, String.valueOf(encrypt));
    }

}
