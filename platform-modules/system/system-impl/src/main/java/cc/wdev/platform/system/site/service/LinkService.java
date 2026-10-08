package cc.wdev.platform.system.site.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.site.domain.entity.LinkEntity;
import cc.wdev.platform.system.site.domain.form.LinkForm;
import cc.wdev.platform.system.site.domain.request.LinkSearchRequest;
import cc.wdev.platform.system.site.domain.vo.LinkVo;
import cc.wdev.platform.system.site.repository.LinkRepository;
import org.springframework.data.domain.Page;

/**
 * @author irving
 */
public interface LinkService extends CachingEntityService<LinkEntity, Long>, EnhancedEntityService<LinkEntity, Long, LinkRepository> {

    void saveLink(LinkForm linkForm);

    void getExtra(LinkVo entity);

    /**
     * 移动端获取友情链接
     */
    Page<LinkVo> friendLinkList(LinkSearchRequest request);

    /**
     * 根据标题查询友情链接列表
     */
    Page<LinkVo> friendLinksByKeyword(LinkSearchRequest request);

    LinkVo getLink(Long id);

    void deleteLink(Long id);
}
