package cc.wdev.platform.system.site.api;

import cc.wdev.platform.system.site.domain.form.AnnouncementForm;
import cc.wdev.platform.system.site.domain.request.AnnouncementSearchRequest;
import cc.wdev.platform.system.site.domain.vo.AnnouncementVo;
import cc.wdev.platform.system.site.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementApiImpl implements AnnouncementApi {

    private final AnnouncementService announcementService;

    @Override
    public Page<AnnouncementVo> findPageAnnouncement(AnnouncementSearchRequest searchRequest) {
        return announcementService.findPageAnnouncement(searchRequest);
    }

    @Override
    public AnnouncementVo getAnnouncement(Long id) {
        return announcementService.getAnnouncement(id);
    }

    @Override
    public void saveAnnouncement(AnnouncementForm form) {
        announcementService.saveAnnouncement(form);
    }

    @Override
    public void deleteAnnouncementBatchById(List<Long> ids) {
        announcementService.softDeleteBatchById(ids);
    }
}
