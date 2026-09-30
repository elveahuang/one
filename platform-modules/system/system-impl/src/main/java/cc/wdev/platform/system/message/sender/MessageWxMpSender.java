package cc.wdev.platform.system.message.sender;

import cc.wdev.platform.commons.enums.SocialTypeEnum;
import cc.wdev.platform.commons.oapis.weixin.service.WxMpManager;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.core.domain.entity.EntityOpenIdEntity;
import cc.wdev.platform.system.core.service.EntityOpenIdService;
import cc.wdev.platform.system.message.MessageSender;
import cc.wdev.platform.system.message.domain.dto.MessageRecipientDto;
import cc.wdev.platform.system.message.domain.dto.SendMessageDto;
import cc.wdev.platform.system.message.service.MessageContentService;
import cc.wdev.platform.system.open.api.WxMpApi;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@Service
public class MessageWxMpSender implements MessageSender {

    private WxMpApi wxMpApi;

    private WxMpManager wxMpManager;

    private EntityOpenIdService entityOpenIdService;

    private MessageContentService messageContentService;

    @Override
    public void send(SendMessageDto message) {
        // 检查企微服务是否已经启动
        if (this.wxMpManager == null) {
            log.info("Send wechat message. message id [{}]. message content id [{}]. failed. wechat is disabled. ", message.getId(), message.getContentId());
            return;
        }

        if (CollectionUtils.isEmpty(message.getParams())) {
            log.info("Send wechat message. message id [{}]. message content id [{}]. failed. params is empty. ", message.getId(), message.getContentId());
            return;
        }

        for (MessageRecipientDto recipient : message.getRecipients()) {
            EntityOpenIdEntity entityOpenIdEntity = entityOpenIdService.findEntityByBizId(SocialTypeEnum.WECHAT_MP.getValue(), recipient.getId());
            if (entityOpenIdEntity == null || StringUtils.isBlank(entityOpenIdEntity.getOpenId())) {
                continue;
            }
            log.info("Send wechat message. message id [{}]. message content id [{}]. start", message.getId(), message.getContentId());
            WxMpService wxMpService = wxMpManager.getService(this.wxMpApi.getAppConfig());
            WxMpTemplateMsgService templateMsgService = wxMpService.getTemplateMsgService();
            List<WxMpTemplateData> dataList = Lists.newArrayListWithCapacity(message.getParams().size());
            message.getParams().forEach((key, value) -> {
                WxMpTemplateData data = new WxMpTemplateData();
                data.setName(key);
                data.setValue((String) value);
                dataList.add(data);
            });

            try {
                String messageId = templateMsgService.sendTemplateMsg(WxMpTemplateMessage.builder()
                    .templateId(message.getContent())
                    .toUser(entityOpenIdEntity.getOpenId())
                    .url(message.getUrl())
                    .data(dataList)
                    .build());
                log.info("Send wechat message. message id [{}]. message content id [{}]. messageId - [{}].", message.getId(), message.getContentId(), messageId);
                // 设置消息内容发送状态
                this.messageContentService.success(message.getContentId(), messageId);
                log.info("Send wechat message. message id [{}]. message content id [{}]. done.", message.getId(), message.getContentId());
            } catch (WxErrorException e) {
                // 设置消息内容发送状态
                this.messageContentService.fail(message.getContentId(), GsonUtils.toJson(e.getError()));
                log.info("Send wechat message. message id [{}]. message content id [{}]. done.", message.getId(), message.getContentId());
            }

        }
    }

    @Autowired
    public void setConfigApi(WxMpApi wxMpApi) {
        this.wxMpApi = wxMpApi;
    }

    @Autowired
    public void setEntityOpenIdService(EntityOpenIdService entityOpenIdService) {
        this.entityOpenIdService = entityOpenIdService;
    }

    @Autowired(required = false)
    public void setWxMpManager(WxMpManager wxMpManager) {
        this.wxMpManager = wxMpManager;
    }

    @Autowired
    public void setMessageContentService(MessageContentService messageContentService) {
        this.messageContentService = messageContentService;
    }

}
