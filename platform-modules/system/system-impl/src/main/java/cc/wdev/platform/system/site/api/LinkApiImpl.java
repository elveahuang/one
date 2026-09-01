package cc.wdev.platform.system.site.api;

import cc.wdev.platform.system.site.domain.form.LinkForm;
import cc.wdev.platform.system.site.domain.request.LinkSearchRequest;
import cc.wdev.platform.system.site.domain.vo.LinkVo;
import cc.wdev.platform.system.site.service.LinkService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author elvea
 */
@AllArgsConstructor
@Service
public class LinkApiImpl implements LinkApi {

    private final LinkService linkService;

    @Override
    public void saveLink(LinkForm linkForm) {
        linkService.saveLink(linkForm);
    }

    @Override
    public void deleteLink(List<Long> list) {
        list.forEach(linkService::deleteLink);
    }

    @Override
    public LinkVo getLink(Long id) {
        return linkService.getLink(id);
    }

    @Override
    public Page<LinkVo> friendLinksByKeyword(LinkSearchRequest request) {
        return linkService.friendLinksByKeyword(request);
    }

}
