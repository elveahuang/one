package cc.wdev.platform.system.im.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.im.domain.entity.ChatMessageContentEntity;
import cc.wdev.platform.system.im.domain.request.ChatMessageContentRequest;
import cc.wdev.platform.system.im.domain.request.ChatMessageContentSaveRequest;
import cc.wdev.platform.system.im.domain.vo.ChatMessageContentVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author erden
 */
public interface ChatMessageContentService extends CachingEntityService<ChatMessageContentEntity, Long> {

    /**
     * 批量获取消息内容
     */
    Map<Long, ChatMessageContentVo> messageContentMap(Collection<Long> messageIds);

    /**
     * 保存职位内容
     */
    ChatMessageContentVo saveChatMessageContent(ChatMessageContentSaveRequest form);

    /**
     * 批量保存消息内容
     */
    void saveBatchChatMessageContent(List<ChatMessageContentSaveRequest> requests);

    /**
     * 获取职位内容
     */
    ChatMessageContentVo getChatMessageContent(ChatMessageContentRequest request);

    /**
     * 删除职位内容
     */
    void deleteChatMessageContent(ChatMessageContentRequest request);

}
