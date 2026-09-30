package cc.wdev.platform.system.open.controller.webapp;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.enums.WeiXinEventTypeEnum;
import cc.wdev.platform.system.commons.enums.WeiXinMsgTypeEnum;
import cc.wdev.platform.system.commons.strategy.WeiXinEventHandleFactory;
import cc.wdev.platform.system.open.api.WxMpApi;
import cc.wdev.platform.system.open.domain.vo.WxMpAppVo;
import cc.wdev.platform.system.storage.api.AttachmentApi;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentFileVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.web.bind.annotation.*;

import java.io.File;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "WxMpController", description = "微信公众号控制器")
public class WxMpWebController extends AbstractController {

    private final AttachmentApi attachmentApi;

    private final WxMpApi wxMpApi;

    @PermitAll
    @Operation(summary = "获取微信公众号配置")
    @ApiResponse(description = "获取微信公众号配置")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/wx/mp/config")
    public R<WxMpAppVo> getConfig() {
        return R.success(this.wxMpApi.getWxMpApp());
    }

    @PermitAll
    @Operation(summary = "获取微信签名")
    @ApiResponse(description = "获取微信签名")
    @ResponseBody
    @PostMapping(API_V1_PREFIX + "/oapis/wechat/mp/signature")
    public R<?> getSignature(@Parameter(description = "请求URL") @RequestParam("url") String url) {
        try {
            WxJsapiSignature signature = wxMpApi.getService().createJsapiSignature(url);
            return R.success(signature);
        } catch (WxErrorException e) {
            log.error("Get wx signature failed.", e);
            return R.error(e.getMessage());
        }
    }

    @PermitAll
    @Operation(summary = "微信回调接口")
    @ApiResponse(description = "微信回调接口")
    @GetMapping(API_V1_PREFIX + "/oapis/wechat/mp/callback")
    public String callback(@RequestParam("signature") String signature,
                           @RequestParam("timestamp") String timestamp,
                           @RequestParam("nonce") String nonce,
                           @RequestParam("echostr") String echostr) {
        if (wxMpApi.getService().checkSignature(timestamp, nonce, signature)) {
            // 验证成功
            log.info("check wx signature success [{}].", echostr);
            return echostr;
        }
        return "fail";
    }

    @PostMapping(API_V1_PREFIX + "/oapis/wechat/mp/callback")
    public String handleMessage(@RequestBody String xmlData,
                                @RequestParam("signature") String signature,
                                @RequestParam("timestamp") String timestamp,
                                @RequestParam("nonce") String nonce) {
        if (!wxMpApi.getService().checkSignature(timestamp, nonce, signature)) {
            return "";
        }

        WxMpXmlMessage message = WxMpXmlMessage.fromXml(xmlData);
        String openid = message.getFromUser();

        log.info("收到用户消息，OpenID: {}", openid);

        if (WeiXinMsgTypeEnum.EVENT.getValue().equalsIgnoreCase(message.getMsgType())) {
            WeiXinEventTypeEnum eventTypeEnum = BaseEnum.getEnumByValue(
                message.getEvent(), WeiXinEventTypeEnum.class, WeiXinEventTypeEnum.NONE);
            return WeiXinEventHandleFactory.getHandle(eventTypeEnum)
                .handle(message)
                .toXml();
        }

        // 用户发送消息
        log.info("接收到用户发来的消息，OpenID: {}，content: {}", openid, message.getContent());
        WxMpXmlOutMessage outMessage = WxMpXmlOutMessage.TEXT()
            .content("你好！")
            .fromUser(message.getToUser())
            .toUser(openid)
            .build();
        return outMessage.toXml();
    }

    @PostMapping(API_V1_PREFIX + "/oapis/wechat/mp/upload")
    public R<AttachmentFileVo> uploadMedia(AttachmentRequest request, @RequestParam("mediaId") String mediaId) {
        try {
            File file = wxMpApi.getService().getMaterialService().mediaDownload(mediaId);
            AttachmentFileVo attachmentVo = attachmentApi.uploadAttachment(request, file);
            return R.success(attachmentVo);
        } catch (Exception e) {
            throw new ServiceException("OSS upload failed.", e);
        }
    }

}
