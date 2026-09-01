package cc.wdev.platform.system.site.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.site.domain.entity.BannerEntity;
import cc.wdev.platform.system.site.domain.form.BannerForm;
import cc.wdev.platform.system.site.domain.request.BannerSearchRequest;
import cc.wdev.platform.system.site.domain.vo.BannerVo;
import cc.wdev.platform.system.site.domain.vo.webapp.BannerWebappVo;
import cc.wdev.platform.system.site.repository.BannerRepository;
import org.springframework.data.domain.Page;


/**
 * @author elvea
 */
public interface BannerService extends CachingEntityService<BannerEntity, Long>, EnhancedEntityService<BannerEntity, Long, BannerRepository> {

    void saveBanner(BannerForm bannerForm);

    void getExtra(BannerVo banner);

    Page<BannerVo> findBannerForUser(BannerSearchRequest searchRequest);

    Page<BannerEntity> search(BannerSearchRequest searchRequest);

    BannerVo getBanner(Long id);

    BannerWebappVo getWebappBanner(Long id);

    Page<BannerWebappVo> findBannerForWebapp(BannerSearchRequest searchRequest);

    Page<BannerVo> findPageBanner(BannerSearchRequest request);

    void deleteBanner(Long id);
}
