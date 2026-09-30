package cc.wdev.platform.system.message.sender;

import cc.wdev.platform.commons.oapis.weixin.service.WxCpManager;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ExceptionUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.constants.SymbolConstants;
import cc.wdev.platform.system.message.MessageSender;
import cc.wdev.platform.system.message.domain.dto.MessageRecipientDto;
import cc.wdev.platform.system.message.domain.dto.SendMessageDto;
import cc.wdev.platform.system.message.service.MessageContentService;
import cc.wdev.platform.system.open.api.WxCpApi;
import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author elvea
 */
@Slf4j
@Service
public class MessageWxCpSender implements MessageSender {

    private WxCpApi wxCpApi;

    private WxCpManager wxCpManager;

    private MessageContentService messageContentService;

    @Override
    public void send(SendMessageDto message) {
        // 检查企微服务是否已经启动
        if (this.wxCpManager == null) {
            log.info("Send wxcp message. message id [{}]. message content id [{}]. failed. wework is disabled. ", message.getId(), message.getContentId());
            return;
        }

        for (List<MessageRecipientDto> recipients : CollUtil.split(message.getRecipients(), 1000)) {
            Set<String> wxCpUserIds = Sets.newHashSetWithExpectedSize(recipients.size());
            for (MessageRecipientDto recipient : recipients) {
                log.info("Send wxcp message. message id [{}]. message content id [{}]. start", message.getId(), message.getContentId());
                String username = recipient.getUsername();
                String mobile = recipient.getMobileNumber();
                String wxCpUserId = "";
                if (StringUtils.isNotEmpty(username)) {
                    try {
                        // 先检测当前用户在企业微信里面是否存在
                        log.info("Send wxcp message. message id [{}]. message content id [{}]. check user [{}].", message.getId(), message.getContentId(), username);
                        WxCpUser wxCpUser = wxCpApi.getService().getUserService().getById(username);
                        wxCpUserId = wxCpUser.getUserId();
                        wxCpUserIds.add(wxCpUser.getUserId());
                        log.info("Send wxcp message. message id [{}]. message content id [{}]. check user [{}]. valid user [{}].", message.getId(), message.getContentId(), username, wxCpUserIds);
                    } catch (Exception e) {
                        log.info("Send wxcp message. message id [{}]. message content id [{}]. check user [{}]. failed.", message.getId(), message.getContentId(), username);
                    }
                }

                if (StringUtils.isEmpty(wxCpUserId) && StringUtils.isNotEmpty(mobile)) {
                    try {
                        // 先检测当前手机号码在企业微信里面是否存在
                        log.info("Send wxcp message. message id [{}]. message content id [{}]. check mobile [{}].", message.getId(), message.getContentId(), mobile);
                        wxCpUserIds.add(wxCpManager.getUserService().getUserId(mobile));
                        log.info("Send wxcp message. message id [{}]. message content id [{}]. check mobile [{}]. valid user [{}].", message.getId(), message.getContentId(), mobile, wxCpUserIds);
                    } catch (Exception e) {
                        log.info("Send wxcp message. message id [{}]. message content id [{}]. check mobile [{}]. failed.", message.getId(), message.getContentId(), mobile);
                    }
                }


            }
            if (CollectionUtils.isEmpty(wxCpUserIds)) {
                log.info("Send wxcp message. message id [{}]. message content id [{}]. invalid user.", message.getId(), message.getContentId());
                return;
            }

            try {
                // 发送企微消息
                WxCpMessage wxCpMessage = WxCpMessage
                    .TEXT()
                    .agentId(wxCpManager.getConfigStorage().getAgentId())
                    .toUser(String.join(SymbolConstants.PIPE, wxCpUserIds))
                    .content(message.getContent())
                    .build();
                WxCpMessageSendResult result = this.wxCpApi.getService().getMessageService().send(wxCpMessage);

                log.info("Send wxcp message. message id [{}]. message content id [{}]. result - [{}].", message.getId(), message.getContentId(), GsonUtils.toJson(result));
                if (result.getErrCode() == 0) {
                    // 设置消息内容发送状态
                    this.messageContentService.success(message.getContentId(), GsonUtils.toJson(result));
                    log.info("Send wxcp message. message id [{}]. message content id [{}]. done.", message.getId(), message.getContentId());
                } else {
                    // 设置消息内容发送状态
                    this.messageContentService.fail(message.getContentId(), GsonUtils.toJson(result));
                    log.info("Send wxcp message. message id [{}]. message content id [{}]. failed.", message.getId(), message.getContentId());
                }
            } catch (Exception e) {
                // 设置消息内容发送状态
                this.messageContentService.fail(message.getContentId(), "", ExceptionUtils.getStackTraceAsString(e));
                log.error("Send wxcp message. message id [{}]. message content id [{}]. failed.", message.getId(), message.getContentId(), e);
            }
        }
    }

    @Autowired(required = false)
    public void setWeiXinCpService(WxCpManager wxCpManager) {
        this.wxCpManager = wxCpManager;
    }

    @Autowired
    public void setMessageContentService(MessageContentService messageContentService) {
        this.messageContentService = messageContentService;
    }

}
