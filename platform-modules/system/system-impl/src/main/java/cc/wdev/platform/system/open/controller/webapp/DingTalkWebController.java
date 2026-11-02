package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.oapis.dingtalk.bean.JsapiSignature;
import cc.wdev.platform.commons.oapis.dingtalk.service.DingTalkService;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.util.CallbackCryptoUtils;
import cc.wdev.platform.system.open.api.DingtalkApi;
import cc.wdev.platform.system.open.domain.vo.DingtalkAppVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * 钉钉控制器。
 *
 * <p>回调协议：钉钉带 {@code timestamp} + {@code sign} 请求头，签名 = Base64(HmacSHA256(timestamp + "\n", appSecret))；
 * 加密模式下报文体为 {@code {"encrypt": "..."}}，用 EncodingAESKey 做 AES-256-CBC 解密。</p>
 *
 * @author elvea
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "DingTalkController", description = "钉钉控制器")
public class DingTalkWebController extends AbstractController {

    private final ObjectProvider<DingTalkService> dingTalkServiceProvider;

    private final DingtalkApi dingtalkApi;

    @org.springframework.beans.factory.annotation.Value("${platform.oapis.dingtalk.encrypt-key:}")
    private String encryptKey;

    @PermitAll
    @Operation(summary = "获取钉钉签名")
    @ApiResponse(description = "获取钉钉签名")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/dingtalk/signature")
    public R<JsapiSignature> getSignature(@Parameter(description = "请求url") @RequestParam("url") String url) throws Exception {
        DingTalkService service = this.dingTalkServiceProvider.getIfAvailable();
        if (service == null) {
            throw new ServiceException(ResponseCodeEnum.OAPI__NOT_IMPLEMENTED);
        }
        log.info("get dingtalk signature for url [{}]", url);
        return R.success(service.createJsapiSignature(url));
    }

    @PermitAll
    @Operation(summary = "获取钉钉配置")
    @ApiResponse(description = "获取钉钉配置")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/dingtalk/config")
    public R<DingtalkAppVo> getConfig() {
        return R.success(this.dingtalkApi.getDingtalkApp());
    }

    /**
     * 钉钉回调：验签（可选，未配置 appSecret 时跳过）后解密报文并分发。
     *
     * <p>回调由钉钉服务器发起，无法携带本系统令牌，安全性由签名/解密保证。</p>
     */
    @PermitAll
    @Operation(summary = "钉钉事件回调")
    @ApiResponse(description = "钉钉事件回调")
    @PostMapping(value = API_V1_PREFIX + "/oapis/dingtalk/callback", produces = "application/json")
    public Map<String, String> callback(
        @RequestHeader(value = "timestamp", required = false) String timestamp,
        @RequestHeader(value = "sign", required = false) String sign,
        @RequestBody(required = false) String body) {
        if (StringUtils.isEmpty(body)) {
            return Map.of("success", Boolean.TRUE.toString());
        }
        String encryptKey = this.dingTalkEncryptKey();
        // 配置了密钥则强制验签，未配置（本地未接入）时仅记录
        if (StringUtils.isNotEmpty(encryptKey) && StringUtils.isNotEmpty(timestamp)) {
            String expected = CallbackCryptoUtils.dingTalkSignature(timestamp, encryptKey);
            // 钉钉 sign 是 URL 编码过的 Base64，先还原
            String actual = sign == null ? null : java.net.URLDecoder.decode(sign, java.nio.charset.StandardCharsets.UTF_8);
            if (!CallbackCryptoUtils.safeEquals(expected, actual)) {
                log.warn("DingTalk callback signature mismatch. timestamp - [{}].", timestamp);
                return Map.of("success", Boolean.FALSE.toString(), "message", "invalid signature");
            }
        }
        try {
            String plain = this.resolvePlainBody(encryptKey, body);
            log.info("DingTalk callback received. content - [{}].", plain);
            this.dispatch(plain);
        } catch (Exception e) {
            log.error("DingTalk callback decrypt failed.", e);
            return Map.of("success", Boolean.FALSE.toString(), "message", "decrypt failed");
        }
        // 钉钉要求回调返回 {"success": true}
        return Map.of("success", Boolean.TRUE.toString());
    }

    /**
     * 明文（加密模式下解密 encrypt 字段）分发扩展点
     */
    protected void dispatch(String plainBody) {
        // 预留扩展：接入钉钉事件时按 EventType 分发
    }

    private String resolvePlainBody(String encryptKey, String body) {
        if (StringUtils.isEmpty(encryptKey)) {
            return body;
        }
        try {
            Map<String, Object> payload = JacksonUtils.toMap(body);
            Object encrypt = payload == null ? null : payload.get("encrypt");
            if (encrypt == null) {
                // 未加密报文（兼容“无加密模式”）
                return body;
            }
            return CallbackCryptoUtils.decrypt(encryptKey, String.valueOf(encrypt));
        } catch (Exception e) {
            throw new IllegalArgumentException("Read dingtalk callback payload failed", e);
        }
    }

    /**
     * 回调加解密密钥，取自 `platform.oapis.dingtalk.encrypt-key`（与钉钉后台 EncodingAESKey 一致）；
     * 为空表示未接入加密回调，此时跳过验签与解密只记录报文。
     */
    private String dingTalkEncryptKey() {
        return this.encryptKey;
    }

}
