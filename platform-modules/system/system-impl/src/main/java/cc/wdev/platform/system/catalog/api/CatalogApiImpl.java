package cc.wdev.platform.system.catalog.api;

import cc.wdev.platform.system.catalog.domain.request.CatalogDeleteRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogSaveRequest;
import cc.wdev.platform.system.catalog.domain.vo.CatalogVo;
import cc.wdev.platform.system.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogApiImpl implements CatalogApi {

    private final CatalogService catalogService;

    @Override
    public CatalogVo saveCatalog(CatalogSaveRequest saveDto) {
        return catalogService.saveCatalog(saveDto);
    }

    @Override
    public void deleteCatalog(CatalogDeleteRequest deleteDto) {
        catalogService.deleteCatalog(deleteDto);
    }

    @Override
    public CatalogVo getCatalog(Long id) {
        return catalogService.getCatalog(id);
    }

    @Override
    public CatalogVo getRootCatalog(String bizType, Long bizId) {
        return catalogService.getRootCatalog(bizType, bizId);
    }

    @Override
    public Page<CatalogVo> findCatalogList(CatalogRequest request) {
        return catalogService.getCatalogList(request);
    }
}
