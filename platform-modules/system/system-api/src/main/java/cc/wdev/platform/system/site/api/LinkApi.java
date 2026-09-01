package cc.wdev.platform.system.site.api;

import cc.wdev.platform.system.site.domain.form.LinkForm;
import cc.wdev.platform.system.site.domain.request.LinkSearchRequest;
import cc.wdev.platform.system.site.domain.vo.LinkVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface LinkApi {

    void saveLink(LinkForm linkForm);

    void deleteLink(List<Long> list);

    LinkVo getLink(Long id);

    Page<LinkVo> friendLinksByKeyword(LinkSearchRequest request);
}
