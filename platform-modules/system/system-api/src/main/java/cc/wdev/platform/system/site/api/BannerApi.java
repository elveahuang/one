package cc.wdev.platform.system.site.api;


import cc.wdev.platform.system.site.domain.form.BannerForm;
import cc.wdev.platform.system.site.domain.request.BannerSearchRequest;
import cc.wdev.platform.system.site.domain.vo.BannerVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface BannerApi {

    void saveBanner(BannerForm bannerForm);

    BannerVo getBanner(Long id);

    void deleteBanner(List<Long> list);

    Page<BannerVo> findPageBanner(BannerSearchRequest request);
}
