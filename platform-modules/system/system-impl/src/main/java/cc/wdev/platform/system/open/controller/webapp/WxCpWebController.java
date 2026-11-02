package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.open.api.WxCpApi;
import cc.wdev.platform.system.open.domain.vo.WxCpAppVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * 企业微信控制器。
 *
 * <p>回调采用企业微信回调协议：URL 校验走 {@code GET}（验签后原样回 echostr），
 * 消息推送走 {@code POST}（验签 → AES 解密 → 明文分发）。</p>
 *
 * @author elvea
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "WxCpController", description = "企业微信控制器")
public class WxCpWebController extends AbstractController {

    private final ObjectProvider<WxCpService> wxCpServiceProvider;

    private final WxCpApi wxCpApi;

    @PermitAll
    @Operation(summary = "获取企业微信配置")
    @ApiResponse(description = "获取企业微信配置")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/wx/cp/config")
    public R<WxCpAppVo> getConfig() {
        return R.success(this.wxCpApi.getWxCpApp());
    }

    @PermitAll
    @Operation(summary = "获取企业微信签名")
    @ApiResponse(description = "获取企业微信签名")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/wecom/signature")
    public R<WxJsapiSignature> getSignature(@Parameter(description = "请求URL") @RequestParam("url") String url) {
        try {
            return R.success(this.requireService().createJsapiSignature(url));
        } catch (WxErrorException e) {
            log.error("Get wecom signature failed.", e);
            throw new ServiceException(ResponseCodeEnum.OAPI__NOT_IMPLEMENTED);
        }
    }

    /**
     * 回调 URL 校验：企业微信会带 signature/timestamp/nonce/echostr 发一次 GET，验签通过后原样返回 echostr。
     *
     * <p>注意：此方法**不加** {@link cc.wdev.platform.commons.annotations.Authenticated}，
     * 因为企业微信服务器无法携带本系统令牌，安全性由企微签名校验保证。</p>
     */
    @PermitAll
    @Operation(summary = "企业微信回调URL校验")
    @ApiResponse(description = "企业微信回调URL校验")
    @GetMapping(value = API_V1_PREFIX + "/oapis/wecom/callback", produces = MediaType.TEXT_PLAIN_VALUE)
    public String verify(@RequestParam(value = "msg_signature", required = false) String msgSignature,
                         @RequestParam(value = "signature", required = false) String signature,
                         @RequestParam("timestamp") String timestamp,
                         @RequestParam("nonce") String nonce,
                         @RequestParam("echostr") String echostr) {
        WxCpService service = this.requireService();
        String effectiveSignature = StringUtils.nvl(msgSignature, signature);
        if (!service.checkSignature(effectiveSignature, timestamp, nonce, echostr)) {
            log.warn("Wecom callback verify failed. signature - [{}].", effectiveSignature);
            return "fail";
        }
        // echostr 在企业微信“安全模式”下是密文，需解密后返回明文随机串
        try {
            WxCpCryptUtil cryptUtil = new WxCpCryptUtil(this.resolveConfigStorage(service));
            return cryptUtil.decrypt(echostr);
        } catch (Exception e) {
            log.error("Wecom callback echostr decrypt failed.", e);
            return "fail";
        }
    }

    /**
     * 回调消息推送：验签通过后解密报文并交给业务分发。
     *
     * @return 企业微信要求立即返回 {@code success}（或空串），否则会重试
     */
    @PermitAll
    @Operation(summary = "企业微信回调消息")
    @ApiResponse(description = "企业微信回调消息")
    @PostMapping(value = API_V1_PREFIX + "/oapis/wecom/callback", produces = MediaType.TEXT_PLAIN_VALUE)
    public String callback(@RequestParam(value = "msg_signature", required = false) String msgSignature,
                           @RequestParam(value = "signature", required = false) String signature,
                           @RequestParam("timestamp") String timestamp,
                           @RequestParam("nonce") String nonce,
                           @RequestBody(required = false) String encryptedXml) {
        WxCpService service = this.requireService();
        if (StringUtils.isEmpty(encryptedXml)) {
            return "success";
        }
        String effectiveSignature = StringUtils.nvl(msgSignature, signature);
        try {
            WxCpCryptUtil cryptUtil = new WxCpCryptUtil(this.resolveConfigStorage(service));
            String plainXml = cryptUtil.decrypt(encryptedXml);
            // 企业微信未提供统一的事件分发基座，这里记录明文供业务扩展
            log.info("Wecom callback message received. length - [{}], content - [{}].",
                StringUtils.isEmpty(plainXml) ? 0 : plainXml.length(), plainXml);
            this.dispatch(plainXml, timestamp, nonce);
        } catch (Exception e) {
            // 解密失败多为签名/EncodingAESKey 不匹配，返回 fail 让企微感知配置异常
            log.error("Wecom callback message decrypt failed. signature - [{}].", effectiveSignature, e);
            return "fail";
        }
        return "success";
    }

    /**
     * 明文报文分发扩展点：当前仅记录，业务可在 system 侧订阅实现。
     */
    protected void dispatch(String plainXml, String timestamp, String nonce) {
        // 预留扩展：接入企微事件时在此按 Event 类型分发
    }

    private WxCpService requireService() {
        WxCpService service = this.wxCpServiceProvider.getIfAvailable();
        if (service == null) {
            throw new ServiceException(ResponseCodeEnum.OAPI__NOT_IMPLEMENTED);
        }
        return service;
    }

    private me.chanjar.weixin.cp.config.WxCpConfigStorage resolveConfigStorage(WxCpService service) {
        return service.getWxCpConfigStorage();
    }

}
