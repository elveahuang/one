package cc.wdev.platform.system.message.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.message.domain.entity.NoticeEntity;
import cc.wdev.platform.system.message.request.NoticeSearchRequest;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 */
public interface NoticeService extends EntityService<NoticeEntity, Long> {

    /**
     * 获取当前登录用户的系统通知列表
     */
    Page<NoticeEntity> findMyNoticeByUserId(NoticeSearchRequest request);

    /**
     * 收件人查看公告详情，非本人收件人返回 null
     */
    NoticeEntity findDetailsForRecipient(Long id);

    /**
     * 管理端公告分页列表（租户内全量，不按收件人过滤）
     */
    Page<NoticeEntity> findNoticeByPage(NoticeSearchRequest noticeSearchRequest);

}
