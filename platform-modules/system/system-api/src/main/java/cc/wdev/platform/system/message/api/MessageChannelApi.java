package cc.wdev.platform.system.message.api;

import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.system.message.domain.vo.MessageChannelVo;
import cc.wdev.platform.system.message.request.MessageChannelRequest;
import cc.wdev.platform.system.message.request.MessageChannelSaveRequest;
import cc.wdev.platform.system.message.request.MessageChannelSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface MessageChannelApi {

    /**
     * 获取消息类型列表
     */
    Page<MessageChannelVo> findMessageChannel(PageRequest request);

    /**
     * 获取消息类型列表
     */
    List<MessageChannelVo> search(MessageChannelSearchRequest request);

    /**
     * 获取消息通道
     */
    MessageChannelVo getMessageChannel(MessageChannelRequest request);

    /**
     * 保存消息通道
     */
    void saveMessageChannel(MessageChannelSaveRequest form);

}
