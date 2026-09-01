package cc.wdev.platform.system.site.api;

import cc.wdev.platform.system.site.domain.form.BannerForm;
import cc.wdev.platform.system.site.domain.request.BannerSearchRequest;
import cc.wdev.platform.system.site.domain.vo.BannerVo;
import cc.wdev.platform.system.site.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerApiImpl implements BannerApi {

    private final BannerService bannerService;

    @Override
    public void saveBanner(BannerForm bannerForm) {
        bannerService.saveBanner(bannerForm);
    }

    @Override
    public BannerVo getBanner(Long id) {
        return bannerService.getBanner(id);
    }

    @Override
    public void deleteBanner(List<Long> list) {
        if (!list.isEmpty()) {
            list.forEach(bannerService::deleteBanner);
        }
    }

    @Override
    public Page<BannerVo> findPageBanner(BannerSearchRequest request) {
        return bannerService.findPageBanner(request);
    }
}
