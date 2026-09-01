package cc.wdev.platform.system.site.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.site.domain.entity.AnnouncementEntity;
import cc.wdev.platform.system.site.domain.form.AnnouncementForm;
import cc.wdev.platform.system.site.domain.request.AnnouncementSearchRequest;
import cc.wdev.platform.system.site.domain.vo.AnnouncementVo;
import cc.wdev.platform.system.site.repository.AnnouncementRepository;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 */
public interface AnnouncementService
    extends CachingEntityService<AnnouncementEntity, Long>, EnhancedEntityService<AnnouncementEntity, Long, AnnouncementRepository> {

    /**
     * 保存公告
     */
    void saveAnnouncement(AnnouncementForm form);

    /**
     * 前端接口查询公告
     */
    Page<AnnouncementVo> findAnnouncementList(AnnouncementSearchRequest request);

    /**
     * 根据关键词返回公告
     */
    Page<AnnouncementEntity> announcementsByKeyword(AnnouncementSearchRequest request);

    /**
     * 根据查询条件返回公告分页列表
     */
    Page<AnnouncementVo> findPageAnnouncement(AnnouncementSearchRequest searchRequest);

    /**
     * 获取公告资讯详情
     */
    AnnouncementVo getAnnouncement(Long id);
}
