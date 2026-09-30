package cc.wdev.platform.system.message.api;

import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.system.message.domain.vo.MessageTypeVo;
import cc.wdev.platform.system.message.request.MessageTypeRequest;
import cc.wdev.platform.system.message.request.MessageTypeSaveRequest;
import cc.wdev.platform.system.message.request.MessageTypeSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface MessageTypeApi {

    /**
     * 获取消息类型列表
     */
    Page<MessageTypeVo> findMessageType(PageRequest request);

    /**
     * 搜索消息类型列表
     */
    List<MessageTypeVo> search(MessageTypeSearchRequest request);

    /**
     * 获取消息类型
     */
    MessageTypeVo getMessageType(MessageTypeRequest request);

    /**
     * 保存消息类型
     */
    void saveMessageType(MessageTypeSaveRequest form);

}
