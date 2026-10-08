package cc.wdev.platform.system.site.api;

import cc.wdev.platform.system.site.domain.form.AnnouncementForm;
import cc.wdev.platform.system.site.domain.request.AnnouncementSearchRequest;
import cc.wdev.platform.system.site.domain.vo.AnnouncementVo;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AnnouncementApi {
    /**
     * 分页查询公告
     */
    Page<AnnouncementVo> findPageAnnouncement(AnnouncementSearchRequest searchRequest);

    /**
     * 获取公告资讯详情
     */
    AnnouncementVo getAnnouncement(Long id);

    void saveAnnouncement(@Valid AnnouncementForm form);

    void deleteAnnouncementBatchById(List<Long> ids);
}
